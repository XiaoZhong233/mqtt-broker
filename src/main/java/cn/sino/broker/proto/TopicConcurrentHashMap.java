package cn.sino.broker.proto;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class TopicConcurrentHashMap extends ConcurrentHashMap<String, HashSet<String>> {

    @Override
    public HashSet<String> get(Object key) {
        String topic = (String) key;

        // 先获取精确匹配的主题的客户端集合
        HashSet<String> exactMatchClients = super.get(topic);
        if (exactMatchClients == null) {
            exactMatchClients = new HashSet<>();
        }

        // 处理通配符匹配的客户端集合
        HashSet<String> wildcardMatchClients = getWildcardClients(topic);

        // 合并精确匹配和通配符匹配的结果
        HashSet<String> allClients = new HashSet<>(exactMatchClients);
        allClients.addAll(wildcardMatchClients);

        return allClients;
    }


    /**
     * 根据 clientId 查询所属的包含通配符的 topic
     *
     * @param clientId 客户端ID
     * @return 包含该 clientId 且包含通配符的 topic 集合
     */
    public Set<String> getWildcardsTopicsByClientId(String clientId) {
        Set<String> topics = new HashSet<>();

        // 遍历所有的 key 和 value
        for (Entry<String, HashSet<String>> entry : this.entrySet()) {
            String topic = entry.getKey();
            HashSet<String> clients = entry.getValue();

            // 检查 topic 是否包含通配符 "#" 或 "+"
            if ((topic.contains("#") || topic.contains("+")) && clients != null && clients.contains(clientId)) {
                topics.add(topic);
            }
        }

        return topics;
    }

    /**
     * 根据 clientId 和 topic 查询匹配的通配符主题
     * @param clientId
     * @param topic
     * @return
     */
    public Set<String> getMatchWildcardsTopic(String clientId, String topic){
        Set<String> wildcardsTopics = getWildcardsTopicsByClientId(clientId);
        return wildcardsTopics.stream()
                .filter(t -> isHashWildcardMatch(t, topic) || isPlusWildcardMatch(t, topic))
                .collect(Collectors.toSet());
    }

    /**
     * 处理通配符匹配，返回符合通配符规则的客户端集合
     */
    private HashSet<String> getWildcardClients(String topic) {
        HashSet<String> wildcardClients = new HashSet<>();

        // 获取所有的主题
        Set<String> allTopics = this.keySet();

        // 处理 `#` 通配符的情况
        Set<String> matchingHashTopics = allTopics.stream()
                .filter(t -> isHashWildcardMatch(t, topic))
                .collect(Collectors.toSet());

        // 处理 `+` 通配符的情况
        Set<String> matchingPlusTopics = allTopics.stream()
                .filter(t -> isPlusWildcardMatch(t, topic))
                .collect(Collectors.toSet());

        // 收集符合通配符规则的客户端集合
        for (String matchedTopic : matchingHashTopics) {
            HashSet<String> clients = super.get(matchedTopic);
            if (clients != null) {
                wildcardClients.addAll(clients);
            }
        }

        for (String matchedTopic : matchingPlusTopics) {
            HashSet<String> clients = super.get(matchedTopic);
            if (clients != null) {
                wildcardClients.addAll(clients);
            }
        }

        return wildcardClients;
    }

    /**
     * 判断是否符合 `#` 通配符规则
     */
    private boolean isHashWildcardMatch(String subscribedTopic, String incomingTopic) {
        if (!subscribedTopic.contains("#")) {
            return false;
        }

        // `#` 只能出现在最后一级，必须在最后
        String[] subLevels = subscribedTopic.split("/");
        String[] inLevels = incomingTopic.split("/");

        // 如果 `#` 出现在最后一级，并且前面的部分匹配，则认为符合规则
        if (subLevels.length > inLevels.length) {
            return false;
        }

        for (int i = 0; i < subLevels.length - 1; i++) {
            if (!subLevels[i].equals(inLevels[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断是否符合 `+` 通配符规则
     */
    private boolean isPlusWildcardMatch(String subscribedTopic, String incomingTopic) {
        String[] subLevels = subscribedTopic.split("/");
        String[] inLevels = incomingTopic.split("/");

        if (subLevels.length != inLevels.length) {
            return false;
        }

        for (int i = 0; i < subLevels.length; i++) {
            if (!subLevels[i].equals("+") && !subLevels[i].equals(inLevels[i])) {
                return false;
            }
        }

        return true;
    }
}
