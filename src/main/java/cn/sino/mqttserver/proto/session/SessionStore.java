package cn.sino.mqttserver.proto.session;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mqtt cleanSession会话存储
 */
@Component
public class SessionStore {
    private Map<String, MqttSession> sessions = new ConcurrentHashMap<>();

    public MqttSession getSession(String clientId) {
        return sessions.get(clientId);
    }

    public void saveSession(String clientId, MqttSession session) {
        sessions.put(clientId, session);
    }

    public void removeSession(String clientId) {
        sessions.remove(clientId);
    }
}

