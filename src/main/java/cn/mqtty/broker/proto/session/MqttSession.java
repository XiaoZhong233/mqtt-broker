package cn.mqtty.broker.proto.session;


import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Data;

import java.util.*;

@Data
public class MqttSession {
    private String clientId;
    private boolean cleanSession;
    private LinkedList<MqttMessage> queuedMessages;
    private boolean lock = false;

    public synchronized void setLock(boolean lock) {
        this.lock = lock;
    }

    public MqttSession(String clientId, boolean cleanSession) {
        this.clientId = clientId;
        this.cleanSession = cleanSession;
        this.queuedMessages = new LinkedList<>();
    }


    public void addQueuedMessage(MqttMessage message) {
        if(!lock){
            queuedMessages.add(message);
        }
    }

    public MqttMessage popQueuedMessage() {
        return queuedMessages.poll();
    }

    // 清理会话数据
    public void clear() {
        queuedMessages.clear();
    }

}
