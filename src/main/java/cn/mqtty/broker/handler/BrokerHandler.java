/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.broker.handler;


import cn.hutool.core.util.StrUtil;
import cn.mqtty.broker.config.BrokerProperties;
import cn.mqtty.broker.protocol.ProtocolProcess;
import cn.mqtty.common.session.SessionStore;
import cn.mqtty.service.evt.DeviceActionEvt;
import cn.mqtty.service.evt.enums.Action;
import cn.mqtty.service.impl.MqttLoggerService;
import cn.mqtty.store.message.DupPubRelMessageStoreService;
import cn.mqtty.store.message.DupPublishMessageStoreService;
import cn.mqtty.store.session.SessionStoreService;
import cn.mqtty.store.subscribe.SubscribeStoreService;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;

/**
 * MQTT消息处理
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {
    @Autowired
    private ProtocolProcess protocolProcess;
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private ChannelGroup channelGroup;
    @Autowired
    private Map<String, ChannelId> channelIdMap;
    @Autowired
    SessionStoreService sessionStoreService;
    @Autowired
    MqttLoggerService mqttLoggerService;
    @Autowired
    SubscribeStoreService subscribeStoreService;
    @Autowired
    DupPublishMessageStoreService dupPublishMessageStoreService;
    @Autowired
    DupPubRelMessageStoreService dupPubRelMessageStoreService;
    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("channel[{}]连接", ctx.channel().id());
        this.channelGroup.add(ctx.channel());
        this.channelIdMap.put(brokerProperties.getId() + "_" + ctx.channel().id().asLongText(), ctx.channel().id());
    }

    private void closeProcess(Channel channel){
        String clientId = (String)channel.attr(AttributeKey.valueOf("clientId")).get();
        String sn = (String) channel.attr(AttributeKey.valueOf("sn")).get();
        log.info("设备{}执行断开的一些处理", sn);
        SessionStore sessionStore = sessionStoreService.get(clientId);
        if (sessionStore != null && sessionStore.isCleanSession()) {
            subscribeStoreService.removeForClient(clientId);
            dupPublishMessageStoreService.removeByClient(clientId);
            dupPubRelMessageStoreService.removeByClient(clientId);
            log.info("设备{}删除订阅缓存、发布缓存");
        }
        mqttLoggerService.info("断开 - clientId: {}, sn:{}, cleanSession: {}", clientId, sn,
                sessionStore!=null?sessionStore.isCleanSession():"null");
        sessionStoreService.remove(clientId);
        mqttLoggerService.logInactive(clientId, channel.id().toString());
        this.channelGroup.remove(channel);
        this.channelIdMap.remove(brokerProperties.getId() + "_" + channel.id().asLongText());
        if(StrUtil.isNotBlank(sn)){
            applicationContext.publishEvent(new DeviceActionEvt(clientId, sn, channel, Action.OFFLINE));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        String clientId = (String) ctx.channel().attr(AttributeKey.valueOf("clientId")).get();
//        SessionStore sessionStore = sessionStoreService.get(clientId);
//        if (sessionStore != null && sessionStore.isCleanSession()) {
//            subscribeStoreService.removeForClient(clientId);
//            dupPublishMessageStoreService.removeByClient(clientId);
//            dupPubRelMessageStoreService.removeByClient(clientId);
//        }
//        mqttLoggerService.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
//        sessionStoreService.remove(clientId);
//        mqttLoggerService.logInactive(clientId, ctx.channel().id().toString());
//        this.channelGroup.remove(ctx.channel());
//        this.channelIdMap.remove(brokerProperties.getId() + "_" + ctx.channel().id().asLongText());
//        String sn = (String) ctx.channel().attr(AttributeKey.valueOf("sn")).get();
//        if(StrUtil.isNotBlank(sn)){
//            applicationContext.publishEvent(new DeviceActionEvt(clientId, sn, ctx.channel(), Action.OFFLINE));
//        }
        log.info("channel[{}]断开", ctx.channel().id());
        closeProcess(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        if (msg.decoderResult().isFailure()) {
            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                ctx.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false),
                        null));
            } else if (cause instanceof MqttIdentifierRejectedException) {
                ctx.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false),
                        null));
            }
            ctx.close();
            return;
        }

        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                protocolProcess.connect().processConnect(ctx.channel(), (MqttConnectMessage) msg);
                break;
            case CONNACK:
                break;
            case PUBLISH:
                protocolProcess.publish().processPublish(ctx.channel(), (MqttPublishMessage) msg);
                break;
            case PUBACK:
                protocolProcess.pubAck().processPubAck(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            case PUBREC:
                protocolProcess.pubRec().processPubRec(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            case PUBREL:
                protocolProcess.pubRel().processPubRel(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            case PUBCOMP:
                protocolProcess.pubComp().processPubComp(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            case SUBSCRIBE:
                protocolProcess.subscribe().processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
                break;
            case SUBACK:
                break;
            case UNSUBSCRIBE:
                protocolProcess.unSubscribe().processUnSubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
                break;
            case UNSUBACK:
                break;
            case PINGREQ:
                protocolProcess.pingReq().processPingReq(ctx.channel(), msg);
                break;
            case PINGRESP:
                break;
            case DISCONNECT:
                protocolProcess.disConnect().processDisConnect(ctx.channel());
                break;
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String clientId = (String)channel.attr(AttributeKey.valueOf("clientId")).get();
        String sn = (String) channel.attr(AttributeKey.valueOf("sn")).get();
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            log.error("IO异常：远程主机{}强迫关闭了一个现有的连接的异常\n sn:{}, clientId:{}.{}", ctx.channel().id(),
                    sn, clientId,cause.getMessage(),cause);
//            protocolProcess.disConnect().processDisConnect(ctx.channel());
            closeProcess(ctx.channel());
            ctx.close();
        } else {
            log.error("异常:{}; sn:{}, clientId:{}",cause.getMessage(), sn, clientId, cause);
            super.exceptionCaught(ctx, cause);
        }
    }

//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent idleStateEvent) {
//            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
//                Channel channel = ctx.channel();
//                String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
//                // 发送遗嘱消息
//                if (this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
//                    SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
//                    if (sessionStore.getWillMessage() != null) {
//                        this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
//                    }
//                }
//                log.info("客户端{}会话过期", clientId);
//                ctx.close();
//            }
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
//    }
}
