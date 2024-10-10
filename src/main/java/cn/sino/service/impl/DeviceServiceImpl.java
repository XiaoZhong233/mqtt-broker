package cn.sino.service.impl;

import cn.sino.broker.protocol.ProtocolProcess;
import cn.sino.service.DeviceService;
import cn.sino.service.evt.DeviceActionEvt;
import cn.sino.store.message.DupPubRelMessageStoreService;
import cn.sino.store.message.DupPublishMessageStoreService;
import cn.sino.store.subscribe.SubscribeStoreService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    SubscribeStoreService subscribeStoreService;
    @Autowired
    DupPublishMessageStoreService dupPublishMessageStoreService;
    @Autowired
    DupPubRelMessageStoreService dupPubRelMessageStoreService;
    @Autowired
    private ProtocolProcess protocolProcess;

    @EventListener
    public void handleActionEvt(DeviceActionEvt deviceActionEvt){
        log.info("设备{}事件：Action: {}, channel:{}", deviceActionEvt.getSn(), deviceActionEvt.getAction(),
                deviceActionEvt.getChannel().id().toString());
        switch (deviceActionEvt.getAction()){
            case ONLINE -> this.online(deviceActionEvt.getChannel(), deviceActionEvt.getSn());
            case OFFLINE -> this.offline(deviceActionEvt.getChannel(), deviceActionEvt.getSn());
        }
    }

    @Override
    public void online(Channel channel, String sn) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                MqttMessageType.PUBLISH,  // 消息类型是PUBLISH
                false,  // DUP flag, 如果是重新发送的消息可以设置为 true
                MqttQoS.AT_LEAST_ONCE,  // QoS 1
                false,  // RETAIN flag, 是否保留消息
                0  // 可变头部和负载的剩余长度（此时设置为 0, 会自动计算）
        );
        String topicName = "$online/operation";
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(
                topicName, 1
        );
        ByteBuf payload = Unpooled.copiedBuffer(sn.getBytes());
        MqttPublishMessage publishMessage = new MqttPublishMessage(
                mqttFixedHeader,
                variableHeader,
                payload
        );
        //发送下线消息
        this.protocolProcess.publish().processPublish(channel, publishMessage);
    }

    @Override
    public void offline(Channel channel, String sn) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                MqttMessageType.PUBLISH,  // 消息类型是PUBLISH
                false,  // DUP flag, 如果是重新发送的消息可以设置为 true
                MqttQoS.AT_LEAST_ONCE,  // QoS 1
                false,  // RETAIN flag, 是否保留消息
                0  // 可变头部和负载的剩余长度（此时设置为 0, 会自动计算）
        );
        String topicName = "$offline/operation";
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(
                topicName, 1
        );
        ByteBuf payload = Unpooled.copiedBuffer(sn.getBytes());
        MqttPublishMessage publishMessage = new MqttPublishMessage(
                mqttFixedHeader,
                variableHeader,
                payload
        );
        //发送下线消息
        this.protocolProcess.publish().processPublish(channel, publishMessage);
    }
}
