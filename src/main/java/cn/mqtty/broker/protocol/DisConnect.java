/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.broker.protocol;


import cn.mqtty.common.message.IDupPubRelMessageStoreService;
import cn.mqtty.common.message.IDupPublishMessageStoreService;
import cn.mqtty.common.session.ISessionStoreService;
import cn.mqtty.common.subscribe.ISubscribeStoreService;
import cn.mqtty.service.impl.MqttLoggerService;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;


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
