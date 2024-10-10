/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.sino.broker.protocol;


import cn.sino.common.message.IDupPubRelMessageStoreService;
import cn.sino.common.message.IDupPublishMessageStoreService;
import cn.sino.common.session.ISessionStoreService;
import cn.sino.common.session.SessionStore;
import cn.sino.common.subscribe.ISubscribeStoreService;
import cn.sino.service.impl.MqttLoggerService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * DISCONNECT连接处理
 */
@Component
public class DisConnect {

    private ISessionStoreService sessionStoreService;
    private ISubscribeStoreService subscribeStoreService;
    private IDupPublishMessageStoreService dupPublishMessageStoreService;
    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;
    private MqttLoggerService loggerService;
//    private ChannelGroup channelGroup;
//    private Map<String, ChannelId> channelIdMap;

    public DisConnect(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService,
                      IDupPublishMessageStoreService dupPublishMessageStoreService,
                      IDupPubRelMessageStoreService dupPubRelMessageStoreService,
                      MqttLoggerService mqttLoggerService) {
        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
        this.loggerService = mqttLoggerService;
//        this.channelGroup = channelGroup;
//        this.channelIdMap = channelIdMap;
    }

    public void processDisConnect(Channel channel) {
//        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
//        SessionStore sessionStore = sessionStoreService.get(clientId);
//        if (sessionStore != null && sessionStore.isCleanSession()) {
//            subscribeStoreService.removeForClient(clientId);
//            dupPublishMessageStoreService.removeByClient(clientId);
//            dupPubRelMessageStoreService.removeByClient(clientId);
//        }
//        loggerService.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
//        sessionStoreService.remove(clientId);
        channel.close();
    }

}
