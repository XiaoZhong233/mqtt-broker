/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.broker.protocol;

import cn.hutool.core.util.StrUtil;
import cn.mqtty.common.message.IMessageIdService;
import cn.mqtty.common.message.IRetainMessageStoreService;
import cn.mqtty.common.message.RetainMessageStore;
import cn.mqtty.common.subscribe.ISubscribeStoreService;
import cn.mqtty.common.subscribe.SubscribeStore;
import cn.mqtty.service.impl.MqttLoggerService;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * SUBSCRIBE连接处理
 */
@Component
public class Subscribe {

//	private static final Logger LOGGER = LoggerFactory.getLogger(Subscribe.class);

	private final ISubscribeStoreService subscribeStoreService;

	private final IMessageIdService messageIdService;

	private final IRetainMessageStoreService retainMessageStoreService;

	private final MqttLoggerService loggerService;


	public Subscribe(ISubscribeStoreService subscribeStoreService, IMessageIdService messageIdService,
					 IRetainMessageStoreService retainMessageStoreService, MqttLoggerService mqttLoggerService) {
		this.subscribeStoreService = subscribeStoreService;
		this.messageIdService = messageIdService;
		this.retainMessageStoreService = retainMessageStoreService;
		this.loggerService = mqttLoggerService;
	}

	public void processSubscribe(Channel channel, MqttSubscribeMessage msg) {
		List<MqttTopicSubscription> topicSubscriptions = msg.payload().topicSubscriptions();
		if (this.validTopicFilter(topicSubscriptions)) {
			String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
			List<Integer> mqttQoSList = new ArrayList<Integer>();
			topicSubscriptions.forEach(topicSubscription -> {
				String topicFilter = topicSubscription.topicName();
				MqttQoS mqttQoS = topicSubscription.qualityOfService();
				SubscribeStore subscribeStore = new SubscribeStore(clientId, topicFilter, mqttQoS.value());
				subscribeStoreService.put(topicFilter, subscribeStore);
				mqttQoSList.add(mqttQoS.value());
				loggerService.info("SUBSCRIBE - clientId: {}, topFilter: {}, QoS: {}", clientId, topicFilter, mqttQoS.value());
			});
			MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
				MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
				new MqttSubAckPayload(mqttQoSList));
			channel.writeAndFlush(subAckMessage);
			// 发布保留消息
			topicSubscriptions.forEach(topicSubscription -> {
				String topicFilter = topicSubscription.topicName();
				MqttQoS mqttQoS = topicSubscription.qualityOfService();
				this.sendRetainMessage(channel, topicFilter, mqttQoS);
			});
		} else {
			channel.close();
		}
	}

	private boolean validTopicFilter(List<MqttTopicSubscription> topicSubscriptions) {
		for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
			String topicFilter = topicSubscription.topicName();
			// 以#或+符号开头的、以/符号结尾的订阅按非法订阅处理, 这里没有参考标准协议
			if (StrUtil.startWith(topicFilter, '+') || StrUtil.endWith(topicFilter, '/'))
				return false;
			if (StrUtil.contains(topicFilter, '#')) {
				// 如果出现多个#符号的订阅按非法订阅处理
				if (StrUtil.count(topicFilter, '#') > 1) return false;
			}
			if (StrUtil.contains(topicFilter, '+')) {
				//如果+符号和/+字符串出现的次数不等的情况按非法订阅处理
				if (StrUtil.count(topicFilter, '+') != StrUtil.count(topicFilter, "/+")) return false;
			}
		}
		return true;
	}

	private void sendRetainMessage(Channel channel, String topicFilter, MqttQoS mqttQoS) {
		List<RetainMessageStore> retainMessageStores = retainMessageStoreService.search(topicFilter);
		retainMessageStores.forEach(retainMessageStore -> {
			MqttQoS respQoS = retainMessageStore.getMqttQoS() > mqttQoS.value() ? mqttQoS : MqttQoS.valueOf(retainMessageStore.getMqttQoS());
			if (respQoS == MqttQoS.AT_MOST_ONCE) {
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, true, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), 0), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				loggerService.info("RETAIN MSG PUBLISH - clientId: {}, topic: {}, Qos: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value());
				channel.writeAndFlush(publishMessage);
			}
			if (respQoS == MqttQoS.AT_LEAST_ONCE) {
				int messageId = messageIdService.getNextMessageId();
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, true, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				loggerService.info("RETAIN MSG PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value(), messageId);
				channel.writeAndFlush(publishMessage);
			}
			if (respQoS == MqttQoS.EXACTLY_ONCE) {
				int messageId = messageIdService.getNextMessageId();
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, true, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				loggerService.info("RETAIN MSG PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value(), messageId);
				channel.writeAndFlush(publishMessage);
			}
		});
	}

}
