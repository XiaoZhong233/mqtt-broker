package cn.mqtty.service;

import java.util.Set;

public interface WsClientActionService {
    void onCustomTopicAllUnsubscribe(String clientId, Set<String> topics);
}
