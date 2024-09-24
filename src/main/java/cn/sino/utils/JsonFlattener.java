package cn.sino.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
