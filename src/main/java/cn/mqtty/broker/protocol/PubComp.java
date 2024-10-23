/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.broker.protocol;

import cn.mqtty.common.message.IDupPubRelMessageStoreService;
import cn.mqtty.service.impl.MqttLoggerService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

/**
 * PUBCOMP连接处理
 */
@Component
public class PubComp {

//	private static final Logger LOGGER = LoggerFactory.getLogger(PubComp.class);

	private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	private final MqttLoggerService loggerService;

	public PubComp(IDupPubRelMessageStoreService dupPubRelMessageStoreService, MqttLoggerService mqttLoggerService) {
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
		this.loggerService = mqttLoggerService;
	}

	public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		loggerService.info("PUBCOMP - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
		dupPubRelMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
	}
}
