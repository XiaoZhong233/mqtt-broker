/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.store.subscribe;

import cn.hutool.core.util.StrUtil;

import cn.mqtty.common.subscribe.ISubscribeStoreService;

import cn.mqtty.common.subscribe.SubscribeStore;
import cn.mqtty.store.cache.SubscribeNotWildcardCache;
import cn.mqtty.store.cache.SubscribeWildcardCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 订阅存储服务
 */
@Component
public class SubscribeStoreService implements ISubscribeStoreService {

    @Autowired
    private SubscribeNotWildcardCache subscribeNotWildcardCache;

    @Autowired
    private SubscribeWildcardCache subscribeWildcardCache;

    @Override
    public void put(String topicFilter, SubscribeStore subscribeStore) {
        if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
            subscribeWildcardCache.put(topicFilter, subscribeStore.getClientId(), subscribeStore);
        } else {
            subscribeNotWildcardCache.put(topicFilter, subscribeStore.getClientId(), subscribeStore);
        }
    }

    @Override
    public void remove(String topicFilter, String clientId) {
        if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
            subscribeWildcardCache.remove(topicFilter, clientId);
        } else {
            subscribeNotWildcardCache.remove(topicFilter, clientId);
        }
    }

    @Override
    public void removeForClient(String clientId) {
        subscribeNotWildcardCache.removeForClient(clientId);
        subscribeWildcardCache.removeForClient(clientId);
    }

    @Override
    public List<SubscribeStore> searchSpecific(String topic) {
        List<SubscribeStore> subscribeStores = new ArrayList<SubscribeStore>();
        List<SubscribeStore> list = subscribeNotWildcardCache.all(topic);
        if (!list.isEmpty()) {
            subscribeStores.addAll(list);
        }
        return subscribeStores;
    }

    @Override
    public List<SubscribeStore> search(String topic) {
        List<SubscribeStore> subscribeStores = new ArrayList<SubscribeStore>();
        List<SubscribeStore> list = subscribeNotWildcardCache.all(topic);
        if (list.size() > 0) {
            subscribeStores.addAll(list);
        }
        subscribeWildcardCache.all().forEach((topicFilter, map) -> {
            if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter, '/').size()) {
                List<String> splitTopics = StrUtil.split(topic, '/');//a
                List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');//#
                String newTopicFilter = "";
                for (int i = 0; i < spliteTopicFilters.size(); i++) {
                    String value = spliteTopicFilters.get(i);
                    if (value.equals("+")) {
                        newTopicFilter = newTopicFilter + "+/";
                    } else if (value.equals("#")) {
                        newTopicFilter = newTopicFilter + "#/";
                        break;
                    } else {
                        newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
                    }
                }
                newTopicFilter = StrUtil.removeSuffix(newTopicFilter, "/");
                if (topicFilter.equals(newTopicFilter)) {
                    Collection<SubscribeStore> collection = map.values();
                    List<SubscribeStore> list2 = new ArrayList<SubscribeStore>(collection);
                    subscribeStores.addAll(list2);
                }
            }
        });
        return subscribeStores;
    }

    /**
     * 获取 clientId 所订阅的 topic 集合
     *
     * @param clientId 客户端ID
     * @return 客户端订阅的 topic 集合
     */
    public Set<String> getSubscribedTopics(String clientId) {
        Set<String> subscribedTopics = new HashSet<>();

        // 遍历 subscribeNotWildcardCache 查找
        subscribeNotWildcardCache.all().forEach((topicFilter, map) -> {
            if (map.containsKey(clientId)) {
                subscribedTopics.add(topicFilter);
            }
        });

        // 遍历 subscribeWildcardCache 查找
        subscribeWildcardCache.all().forEach((topicFilter, map) -> {
            if (map.containsKey(clientId)) {
                subscribedTopics.add(topicFilter);
            }
        });

        return subscribedTopics;
    }

}
