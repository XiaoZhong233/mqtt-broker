package cn.sino.store.cache;


import cn.sino.common.message.DupPublishMessageStore;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class DupPublishMessageCache {
    private final static String CACHE_PRE = "mqttwk:publish:";
    @Autowired
    private StringRedisTemplate redisService;


    public DupPublishMessageStore put(String clientId, Integer messageId, DupPublishMessageStore dupPublishMessageStore) {
        redisService.opsForHash().put(CACHE_PRE + clientId, String.valueOf(messageId), JSONObject.toJSONString(dupPublishMessageStore));
        return dupPublishMessageStore;
    }

    public ConcurrentHashMap<Integer, DupPublishMessageStore> get(String clientId) {
        ConcurrentHashMap<Integer, DupPublishMessageStore> map = new ConcurrentHashMap<>();
        Map<Object, Object> map1 = redisService.opsForHash().entries(CACHE_PRE + clientId);
        if (map1 != null && !map1.isEmpty()) {
            map1.forEach((k, v) -> {
                map.put(Integer.valueOf((String) k), JSONObject.parseObject((String) v, DupPublishMessageStore.class));
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
