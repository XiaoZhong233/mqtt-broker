package cn.mqtty.utils;

import io.netty.util.CharsetUtil;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonFlattener {

    public static String flattenJson(MqttPublishMessage mqttPublishMessage) {
        try {
            String jsonString = mqttPublishMessage.payload().toString(CharsetUtil.UTF_8);
//            JSONObject jsonObject = JSON.parseObject(jsonString);
            return jsonString;
        } catch (Exception e) {
            log.error("flatten json failed, {}",e.getMessage());
            return mqttPublishMessage.payload().toString(CharsetUtil.UTF_8);
        }
    }
}
