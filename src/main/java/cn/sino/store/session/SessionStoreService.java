/**
 * Created by wizzer on 2018
 */

package cn.sino.store.session;


import cn.sino.common.session.ISessionStoreService;
import cn.sino.common.session.SessionStore;
import cn.sino.store.util.StoreUtil;
import com.alibaba.fastjson2.JSON;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 会话存储服务
 */
@Component
public class SessionStoreService implements ISessionStoreService {
    private final static String CACHE_PRE = "mqttwk:session:";
    @Autowired
    private StringRedisTemplate redisService;

    @Override
    public void put(String clientId, SessionStore sessionStore, int expire) {
        //SessionStore对象不能正常转为JSON,使用工具类类解决
        NutMap nutMap = StoreUtil.transPublishToMapBeta(sessionStore);
        if (nutMap != null) {
            if (expire > 0) {
                redisService.opsForValue().set(CACHE_PRE + clientId, JSON.toJSONString(nutMap),expire, TimeUnit.SECONDS);
            } else {
                redisService.opsForValue().set(CACHE_PRE + clientId, JSON.toJSONString(nutMap));
            }
        }

    }

    @Override
    public void expire(String clientId, int expire) {
        redisService.expire(CACHE_PRE + clientId, expire, TimeUnit.SECONDS);
    }


    @Override
    public SessionStore get(String clientId) {
        String jsonObj = redisService.opsForValue().get(CACHE_PRE + clientId);
        if (Strings.isNotBlank(jsonObj)) {
            NutMap nutMap = JSON.parseObject(jsonObj, NutMap.class);
            return StoreUtil.mapTransToPublishMsgBeta(nutMap);
        }
        return null;
    }

    @Override
    public boolean containsKey(String clientId) {
        return Boolean.TRUE.equals(redisService.hasKey(CACHE_PRE + clientId));
    }


    @Override
    @Async
    public void remove(String clientId) {
        redisService.delete(CACHE_PRE + clientId);
    }

}
