package cn.sino.mqttserver.proto.session;

import cn.sino.mqttserver.proto.Subscription;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MqttSession {
    private String clientId;
    private boolean cleanSession;
    private Map<String, Subscription> subscriptions;
    private List<MqttMessage> queuedMessages;

    public MqttSession(String clientId, boolean cleanSession) {
        this.clientId = clientId;
        this.cleanSession = cleanSession;
        this.subscriptions = new HashMap<>();
        this.queuedMessages = new ArrayList<>();
    }

    // 添加订阅
    public void addSubscription(String topic, Subscription subscription) {
        subscriptions.put(topic, subscription);
    }

    // 获取订阅信息
    public Subscription getSubscription(String topic) {
        return subscriptions.get(topic);
    }

    public void addQueuedMessage(MqttMessage message) {
        queuedMessages.add(message);
    }

    // 清理会话数据
    public void clear() {
        subscriptions.clear();
        queuedMessages.clear();
    }

    public void removeSubscription(String topic) {
        subscriptions.remove(topic);

    }
}
