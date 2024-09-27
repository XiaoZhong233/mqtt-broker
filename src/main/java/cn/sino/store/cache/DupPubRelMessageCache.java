package cn.sino.store.cache;


import cn.sino.common.message.DupPubRelMessageStore;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wizzer on 2018
 */
@Component
public class DupPubRelMessageCache {
    private final static String CACHE_PRE = "mqttwk:pubrel:";
    @Autowired
    private StringRedisTemplate redisService;

    public DupPubRelMessageStore put(String clientId, Integer messageId, DupPubRelMessageStore dupPubRelMessageStore) {
        redisService.opsForHash().put(CACHE_PRE + clientId, String.valueOf(messageId), JSONObject.toJSONString(dupPubRelMessageStore));
        return dupPubRelMessageStore;
    }

    public ConcurrentHashMap<Integer, DupPubRelMessageStore> get(String clientId) {
        ConcurrentHashMap<Integer, DupPubRelMessageStore> map = new ConcurrentHashMap<>();
        Map<Object, Object> map1 = redisService.opsForHash().entries(CACHE_PRE + clientId);
        if (!map1.isEmpty()) {
            map1.forEach((k, v) -> {
                map.put(Integer.valueOf((String) k), JSONObject.parseObject(v.toString(), DupPubRelMessageStore.class));
            });
        }
        return map;
    }

    public boolean containsKey(String clientId) {
        return Boolean.TRUE.equals(redisService.hasKey(CACHE_PRE + clientId));
    }

    @Async
    public void remove(String clientId, Integer messageId) {
        redisService.opsForHash().delete(CACHE_PRE + clientId, String.valueOf(messageId));
    }

    @Async
    public void remove(String clientId) {
        redisService.delete(CACHE_PRE + clientId);
    }
}
