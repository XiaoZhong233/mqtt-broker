package cn.sino.store.cache;

import cn.sino.common.subscribe.SubscribeStore;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wizzer on 2018
 */
@Component
public class SubscribeNotWildcardCache {
    private final static String CACHE_PRE = "mqttwk:subnotwildcard:";
    private final static String CACHE_CLIENT_PRE = "mqttwk:client:";
    private final static String CACHE_TOPIC = "mqttwk:subnotwildcard_topic";
    @Autowired
    private StringRedisTemplate redisService;

    public SubscribeStore put(String topic, String clientId, SubscribeStore subscribeStore) {
        redisService.opsForHash().put(CACHE_PRE + topic, clientId, JSONObject.toJSONString(subscribeStore));
        redisService.opsForSet().add(CACHE_CLIENT_PRE + clientId, topic);
        redisService.opsForSet().add(CACHE_TOPIC, topic);
        return subscribeStore;
    }

    public SubscribeStore get(String topic, String clientId) {
        return JSONObject.parseObject((String) redisService.opsForHash().get(CACHE_PRE + topic, clientId), SubscribeStore.class);
    }

    public boolean containsKey(String topic, String clientId) {
        return redisService.opsForHash().hasKey(CACHE_PRE + topic, clientId);
    }

    @Async
    public void remove(String topic, String clientId) {
        redisService.opsForSet().remove(CACHE_CLIENT_PRE + clientId, topic);
        redisService.opsForHash().delete(CACHE_PRE + topic, clientId);
        if (redisService.opsForHash().size(CACHE_PRE + topic) == 0) {
            redisService.opsForSet().remove(CACHE_TOPIC, topic);
        }
    }

    @Async
    public void removeForClient(String clientId) {
        Set<String> members = redisService.opsForSet().members(CACHE_CLIENT_PRE + clientId);
        if(members!=null){
            for (String topic : members) {
                redisService.opsForHash().delete(CACHE_PRE + topic, clientId);
                if (redisService.opsForHash().size(CACHE_PRE + topic) == 0) {
                    redisService.opsForSet().remove(CACHE_TOPIC, topic);
                }
            }
        }
        redisService.delete(CACHE_CLIENT_PRE + clientId);
    }

    public Map<String, ConcurrentHashMap<String, SubscribeStore>> all() {
        Map<String, ConcurrentHashMap<String, SubscribeStore>> map = new HashMap<>();
        Set<String> topics = redisService.opsForSet().members(CACHE_TOPIC);
        if(topics!=null){
            for (String topic : topics) {
                ConcurrentHashMap<String, SubscribeStore> map1 = new ConcurrentHashMap<>();
                Map<Object, Object> map2 = redisService.opsForHash().entries(CACHE_PRE + topic);
                if (map2 != null && !map2.isEmpty()) {
                    map2.forEach((k, v) -> {
                        map1.put(k.toString(), JSONObject.parseObject(v.toString(), SubscribeStore.class));
                    });
                    map.put(topic, map1);
                }
            }
        }
        return map;
    }

    public List<SubscribeStore> all(String topic) {
        List<SubscribeStore> list = new ArrayList<>();
        Map<Object, Object> map = redisService.opsForHash().entries(CACHE_PRE + topic);
        if (!map.isEmpty()) {
            map.forEach((k, v) -> {
                list.add(JSONObject.parseObject(v.toString(), SubscribeStore.class));
            });
        }
        return list;
    }
}
