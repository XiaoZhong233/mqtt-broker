/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.store.message;


import cn.mqtty.common.message.DupPublishMessageStore;
import cn.mqtty.common.message.IDupPublishMessageStoreService;
import cn.mqtty.common.message.IMessageIdService;
import cn.mqtty.store.cache.DupPublishMessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DupPublishMessageStoreService implements IDupPublishMessageStoreService {

    @Autowired
    private IMessageIdService messageIdService;
    @Autowired
    private DupPublishMessageCache dupPublishMessageCache;

    @Override
    public void put(String clientId, DupPublishMessageStore dupPublishMessageStore) {
        dupPublishMessageCache.put(clientId, dupPublishMessageStore.getMessageId(), dupPublishMessageStore);
    }

    @Override
    public List<DupPublishMessageStore> get(String clientId) {
        if (dupPublishMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
            Collection<DupPublishMessageStore> collection = map.values();
            return new ArrayList<>(collection);
        }
        return new ArrayList<>();
    }

    @Override
    public void remove(String clientId, int messageId) {
        dupPublishMessageCache.remove(clientId, messageId);
    }

    @Override
    public void removeByClient(String clientId) {
        dupPublishMessageCache.remove(clientId);
    }
}
