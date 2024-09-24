package cn.sino.service.impl;

import cn.sino.service.DeviceChannelService;
import cn.sino.utils.JsonFlattener;
import io.netty.handler.codec.mqtt.*;
import lombok.AllArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MqttLoggerService {

    DeviceChannelService deviceChannelService;

    private static final Logger mqttLogger = LoggerFactory.getLogger("mqttLog");
    public void info(String message, Object... objects) {
        mqttLogger.info(message, objects);
    }

    public void logActive(String sn, String ip){
        mqttLogger.info("[Login]-(sn:{}, ip:{})", sn, ip);
    }

    public void logInactive(String from){
        mqttLogger.info("[Logout]-(sn:{})", deviceChannelService.getSnByChannelId(from));
    }

    public void logSendSuccess(String from, String to, int qos, String topic, String msg){
        String format = String.format("[Send Success]-(from: %s, to: %s). [Payload]-(%s)",
                deviceChannelService.getSnByChannelId(from), to, formatMqttMsg(qos, topic, msg));
        mqttLogger.info(format);
    }

    public void logSendFailed(String from, String to, String reason, int qos, String topic, String msg){
        String format = String.format("[Send Failed]-(from: %s, to: %s, reason:%s). [Payload]-(%s)",
                deviceChannelService.getSnByChannelId(from), to,reason, formatMqttMsg(qos, topic, msg));
        mqttLogger.error(format);
    }

    public void logSubSuccess(String from, List<MqttTopicSubscription> mqttTopicSubscriptions){
        for (MqttTopicSubscription subscription : mqttTopicSubscriptions){
            String topic = subscription.topicFilter();
            String format = String.format("[Sub success]-(from: %s, topic: %s, qos: %s). ", deviceChannelService.getSnByChannelId(from),
                    topic, subscription.option().qos().value());
            mqttLogger.info(format);
        }
    }

    public void logUnsubSuccess(String from, List<String> topics){
        for (String topic: topics){
            String format = String.format("[Unsub success]-(from: %s, topic: %s). ", deviceChannelService.getSnByChannelId(from),
                    topic);
            mqttLogger.info(format);
        }
    }

//    private String formatMqttMsg(MqttPublishMessage mqttPublishMessage){
//        MqttFixedHeader mqttFixedHeaderInfo = mqttPublishMessage.fixedHeader();
//        MqttQoS qos = mqttFixedHeaderInfo.qosLevel();
//        MqttPublishVariableHeader variableHeader = mqttPublishMessage.variableHeader();
//        String topicName = variableHeader.topicName();
//        return String.format("[Qos]: %s, [Topic]: %s, [Msg]: (%s)", qos.value(), topicName, JsonFlattener.flattenJson(mqttPublishMessage));
//    }

    private String formatMqttMsg(int qos, String topic, String msg){
        return String.format("[Qos]: %s, [Topic]: %s, [Msg]: (%s)", qos, topic, msg);
    }
}
