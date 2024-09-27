package cn.sino.store.cache;

import cn.sino.common.message.RetainMessageStore;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by wizzer on 2018
 */
@Component
public class RetainMessageCache {
    private final static String CACHE_PRE = "mqttwk:retain:";
    private final static String CACHE_TOPIC = "mqttwk:retain_topic";
    @Autowired
    private StringRedisTemplate redisService;

    public RetainMessageStore put(String topic, RetainMessageStore obj) {
        redisService.opsForValue().set(CACHE_PRE + topic, JSONObject.toJSONString(obj));
        redisService.opsForSet().add(CACHE_TOPIC, topic);
        return obj;
    }

    public RetainMessageStore get(String topic) {
        return JSONObject.parseObject(redisService.opsForValue().get(CACHE_PRE + topic), RetainMessageStore.class);
    }

    public boolean containsKey(String topic) {
        return Boolean.TRUE.equals(redisService.hasKey(CACHE_PRE + topic));
    }

    @Async
    public void remove(String topic) {
        redisService.delete(CACHE_PRE + topic);
        redisService.opsForSet().remove(CACHE_TOPIC, topic);
    }

    public Map<String, RetainMessageStore> all() {
        Map<String, RetainMessageStore> map = new HashMap<>();
        Set<String> topics = redisService.opsForSet().members(CACHE_TOPIC);
        if(topics!=null){
            for (String topic : topics) {
                map.put(topic, JSONObject.parseObject(redisService.opsForValue().get(CACHE_PRE + topic), RetainMessageStore.class));
            }
        }
        return map;
    }
}
