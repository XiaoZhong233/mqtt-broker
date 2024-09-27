/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.sino.broker.protocol;

import cn.sino.common.message.IDupPublishMessageStoreService;
import cn.sino.service.impl.MqttLoggerService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * PUBACK连接处理
 */
@Component
public class PubAck {

//	private static final Logger LOGGER = LoggerFactory.getLogger(PubAck.class);
	private final MqttLoggerService loggerService;

	private IDupPublishMessageStoreService dupPublishMessageStoreService;

	public PubAck(IDupPublishMessageStoreService dupPublishMessageStoreService, MqttLoggerService mqttLoggerService) {
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.loggerService = mqttLoggerService;
	}

	public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		loggerService.info("PUBACK - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
		dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
	}

}
