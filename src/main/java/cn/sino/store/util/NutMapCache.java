package cn.sino.store.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.nutz.lang.util.NutMap;

import java.util.concurrent.TimeUnit;



public class NutMapCache {
    private final Cache<String, NutMap> cache;
    @Getter
    private long expire;

    public NutMapCache(long expire) {
        this.expire = expire;
        cache = Caffeine.newBuilder()
                .expireAfterAccess(expire, TimeUnit.MINUTES) // 设置过期时间
                .maximumSize(100000) // 设置最大缓存条目
                .build();
    }

    public void put(String key, NutMap value) {
        cache.put(key, value); // 添加或更新缓存项
    }

    public NutMap get(String key) {
        return cache.getIfPresent(key); // 获取缓存项
    }

    public void remove(String key) {
        cache.invalidate(key); // 删除缓存项
    }

    public boolean containsKey(String key) {
        return cache.asMap().containsKey(key); // 检查缓存中是否存在该键
    }
}
