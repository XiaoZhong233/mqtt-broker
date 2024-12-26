package cn.mqtty.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.mqtty.broker.protocol.ProtocolProcess;
import cn.mqtty.service.WsClientActionService;
import cn.mqtty.service.evt.WsActionEvt;
import cn.mqtty.service.vo.Command;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
public class WsClientActionServiceImpl implements WsClientActionService {
    @Autowired
    private ProtocolProcess protocolProcess;
    @EventListener
    public void handleTopicActionEvt(WsActionEvt actionEvt){
        log.info("WS客户端{}事件：Action: {}, Topics:{}", actionEvt.getClientId(),
                actionEvt.getAction(), actionEvt.getTopics());
        onCustomTopicAllUnsubscribe(actionEvt.getClientId(), actionEvt.getTopics());
    }

    private String generateRandomId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(14);

        for (int i = 0; i < 14; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));  // 随机选择字符
        }

        return sb.toString();
    }

    @Override
    public void onCustomTopicAllUnsubscribe(String clientId, Set<String> topics) {
        for(String topic: topics){
            String sn = extractSn(topic);
            if(sn==null || StrUtil.isBlank(sn)){
                log.error("需要关闭的sn为空或null");
                continue;
            }
            if(topic.startsWith("$remote/client2server")){
                String topicName = "$remote/cmd" + "/" + sn;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 0);
                Command<JSONObject> jsonObjectCommand = new Command<>(sn, "disable_remote_control", jsonObject,
                        generateRandomId(), new Date().getTime());
                this.protocolProcess.publish().sendPublishMessage(topicName, MqttQoS.AT_LEAST_ONCE, JSON.toJSONBytes(jsonObjectCommand),
                        false, false);
                log.info("客户端[{}]发送超级终端终止指令 To: sn[{}]", clientId, sn);
            }
            if(topic.startsWith("$log/operation")){
                String topicName = "$log/cmd" + "/" + sn;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("logLevel", 0);
                Command<JSONObject> jsonObjectCommand = new Command<>(sn, "set_log_upload", jsonObject,
                        generateRandomId(),new Date().getTime());
                this.protocolProcess.publish().sendPublishMessage(topicName, MqttQoS.AT_LEAST_ONCE, JSON.toJSONBytes(jsonObjectCommand),
                        false, false);
                log.info("客户端[{}]发送日志上报终止指令 To: sn[{}]", clientId, sn);
            }
        }
    }

    private String extractSn(String topic){
        if(StrUtil.isNotBlank(topic) && topic.contains("/")){
            int lastIndex = topic.lastIndexOf("/");
            if (lastIndex != -1 && lastIndex < topic.length() - 1) {
                String substring = topic.substring(lastIndex + 1);
                if(StrUtil.isBlank(substring)){
                    return null;
                }
                char firstChar = substring.charAt(0);
                if(Character.isLetterOrDigit(firstChar)){
                    return substring;
                }
            }
        }
        return null;
    }
}
