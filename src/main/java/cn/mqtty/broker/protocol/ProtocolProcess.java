/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.broker.protocol;


import cn.mqtty.broker.config.BrokerProperties;
import cn.mqtty.common.auth.IAuthService;
import cn.mqtty.common.message.IDupPubRelMessageStoreService;
import cn.mqtty.common.message.IDupPublishMessageStoreService;
import cn.mqtty.common.message.IMessageIdService;
import cn.mqtty.common.message.IRetainMessageStoreService;
import cn.mqtty.common.session.ISessionStoreService;
import cn.mqtty.common.subscribe.ISubscribeStoreService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 协议处理
 */
@Component
@Getter
public class ProtocolProcess {

    @Autowired
    private ISessionStoreService sessionStoreService;
    @Autowired
    private ISubscribeStoreService subscribeStoreService;
    @Autowired
    private IAuthService authService;
    @Autowired
    private IMessageIdService messageIdService;
    @Autowired
    private IRetainMessageStoreService messageStoreService;
    @Autowired
    private IDupPublishMessageStoreService dupPublishMessageStoreService;
    @Autowired
    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private Connect connect;
    @Autowired
    private Subscribe subscribe;
    @Autowired
    private UnSubscribe unSubscribe;
    @Autowired
    private Publish publish;
    @Autowired
    private DisConnect disConnect;
    @Autowired
    private PingReq pingReq;
    @Autowired
    private PubRel pubRel;
    @Autowired
    private PubAck pubAck;
    @Autowired
    private PubRec pubRec;
    @Autowired
    private PubComp pubComp;
    //    @Autowired
//    private InternalCommunication internalCommunication;
    public Connect connect() {
        return connect;
    }

    public Subscribe subscribe() {
        return subscribe;
    }

    public UnSubscribe unSubscribe() {
        return unSubscribe;
    }

    public Publish publish() {
        return publish;
    }

    public DisConnect disConnect() {
        return disConnect;
    }

    public PingReq pingReq() {
        return pingReq;
    }

    public PubRel pubRel() {
        return pubRel;
    }

    public PubAck pubAck() {
        return pubAck;
    }

    public PubRec pubRec() {
        return pubRec;
    }

    public PubComp pubComp() {
        return pubComp;
    }

}
