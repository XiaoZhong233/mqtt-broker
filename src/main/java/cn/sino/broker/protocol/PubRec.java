/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.sino.broker.protocol;


import cn.sino.common.message.DupPubRelMessageStore;
import cn.sino.common.message.IDupPubRelMessageStoreService;
import cn.sino.common.message.IDupPublishMessageStoreService;
import cn.sino.service.impl.MqttLoggerService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * PUBREC连接处理
 */
@Component
public class PubRec {

//	private static final Logger LOGGER = LoggerFactory.getLogger(PubRec.class);

	private IDupPublishMessageStoreService dupPublishMessageStoreService;

	private IDupPubRelMessageStoreService dupPubRelMessageStoreService;
	private final MqttLoggerService loggerService;


	public PubRec(IDupPublishMessageStoreService dupPublishMessageStoreService,
				  IDupPubRelMessageStoreService dupPubRelMessageStoreService,
				  MqttLoggerService mqttLoggerService) {
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
		this.loggerService = mqttLoggerService;
	}

	public void processPubRec(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		loggerService.info("PUBREC - clientId: {}, messageId: {}", channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
		dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
		DupPubRelMessageStore dupPubRelMessageStore = new DupPubRelMessageStore().setClientId((String) channel.attr(AttributeKey.valueOf("clientId")).get())
			.setMessageId(variableHeader.messageId());
		dupPubRelMessageStoreService.put((String) channel.attr(AttributeKey.valueOf("clientId")).get(), dupPubRelMessageStore);
		channel.writeAndFlush(pubRelMessage);
	}

}
