/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.broker.protocol;

import cn.mqtty.common.subscribe.ISubscribeStoreService;
import cn.mqtty.service.impl.MqttLoggerService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * UNSUBSCRIBE连接处理
 */
@Component
public class UnSubscribe {

//	private static final Logger LOGGER = LoggerFactory.getLogger(UnSubscribe.class);

	private ISubscribeStoreService subscribeStoreService;

	private final MqttLoggerService loggerService;

	public UnSubscribe(ISubscribeStoreService subscribeStoreService, MqttLoggerService mqttLoggerService) {
		this.subscribeStoreService = subscribeStoreService;
		this.loggerService = mqttLoggerService;
	}

	public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
		List<String> topicFilters = msg.payload().topics();
		String clinetId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		topicFilters.forEach(topicFilter -> {
			subscribeStoreService.remove(topicFilter, clinetId);
			loggerService.info("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clinetId, topicFilter);
		});
		MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
		channel.writeAndFlush(unsubAckMessage);
	}

}
