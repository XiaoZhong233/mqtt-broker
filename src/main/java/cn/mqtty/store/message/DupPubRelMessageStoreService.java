/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.store.message;


import cn.mqtty.common.message.DupPubRelMessageStore;
import cn.mqtty.common.message.IDupPubRelMessageStoreService;
import cn.mqtty.common.message.IMessageIdService;
import cn.mqtty.store.cache.DupPubRelMessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DupPubRelMessageStoreService implements IDupPubRelMessageStoreService {

    @Autowired
    private IMessageIdService messageIdService;

    @Autowired
    private DupPubRelMessageCache dupPubRelMessageCache;

    @Override
    public void put(String clientId, DupPubRelMessageStore dupPubRelMessageStore) {
        dupPubRelMessageCache.put(clientId, dupPubRelMessageStore.getMessageId(), dupPubRelMessageStore);
    }

    @Override
    public List<DupPubRelMessageStore> get(String clientId) {
        if (dupPubRelMessageCache.containsKey(clientId)) {
            ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageCache.get(clientId);
            Collection<DupPubRelMessageStore> collection = map.values();
            return new ArrayList<>(collection);
        }
        return new ArrayList<>();
    }

    @Override
    public void remove(String clientId, int messageId) {
        dupPubRelMessageCache.remove(clientId, messageId);
    }

    @Override
    public void removeByClient(String clientId) {
        if (dupPubRelMessageCache.containsKey(clientId)) {
            dupPubRelMessageCache.remove(clientId);
        }
    }
}
