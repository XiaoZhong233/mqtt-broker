/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.sino.broker.protocol;


import cn.sino.broker.config.BrokerProperties;
import cn.sino.common.auth.IAuthService;
import cn.sino.common.message.IDupPubRelMessageStoreService;
import cn.sino.common.message.IDupPublishMessageStoreService;
import cn.sino.common.message.IMessageIdService;
import cn.sino.common.message.IRetainMessageStoreService;
import cn.sino.common.session.ISessionStoreService;
import cn.sino.common.subscribe.ISubscribeStoreService;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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
