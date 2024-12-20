/**
 * Created by wizzer on 2018
 */

package cn.mqtty.store.session;


import cn.mqtty.broker.config.BrokerProperties;
import cn.mqtty.common.session.ISessionStoreService;
import cn.mqtty.common.session.SessionStore;
import cn.mqtty.store.util.NutMapCache;
import cn.mqtty.store.util.StoreUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
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
    @Autowired
    BrokerProperties brokerProperties;
    private NutMapCache nutMapCache;

    @PostConstruct
    public void init(){
        nutMapCache = new NutMapCache(Math.round(brokerProperties.getKeepAlive()*1.5f));
    }


    @Override
    public void put(String clientId, SessionStore sessionStore, int expire) {
        //SessionStore对象不能正常转为JSON,使用工具类类解决
        NutMap nutMap = StoreUtil.transPublishToMapBeta(sessionStore);
        if (nutMap == null) {
            return;
        }
        if(brokerProperties.getSession_storage_type().equals("redis")){
            if (expire > 0) {
                redisService.opsForValue().set(CACHE_PRE + clientId, JSON.toJSONString(nutMap),expire, TimeUnit.SECONDS);
            } else {
                redisService.opsForValue().set(CACHE_PRE + clientId, JSON.toJSONString(nutMap));
            }
        }else {
            nutMapCache.put(CACHE_PRE + clientId, nutMap);
        }

    }

    @Override
    public void expire(String clientId, int expire) {
        if(brokerProperties.getSession_storage_type().equals("redis")){
            redisService.expire(CACHE_PRE + clientId, expire, TimeUnit.SECONDS);
        }else {
            nutMapCache.get(CACHE_PRE + clientId);
        }
    }


    @Override
    public SessionStore get(String clientId) {
        if(brokerProperties.getSession_storage_type().equals("redis")){
            String jsonObj = redisService.opsForValue().get(CACHE_PRE + clientId);
            if (Strings.isNotBlank(jsonObj)) {
                NutMap nutMap = JSON.parseObject(jsonObj, NutMap.class);
                return StoreUtil.mapTransToPublishMsgBeta(nutMap);
            }
        }else {
            NutMap nutMap = nutMapCache.get(CACHE_PRE + clientId);
            if(nutMap==null){
                return null;
            }
            return StoreUtil.mapTransToPublishMsgBeta(nutMap);
        }
        return null;
    }

    @Override
    public boolean containsKey(String clientId) {
        if(brokerProperties.getSession_storage_type().equals("redis")){
            return Boolean.TRUE.equals(redisService.hasKey(CACHE_PRE + clientId));
        }else {
            return nutMapCache.containsKey(CACHE_PRE + clientId);
        }
    }


    @Override
    @Async
    public void remove(String clientId) {
        if(brokerProperties.getSession_storage_type().equals("redis")){
            redisService.delete(CACHE_PRE + clientId);
        }else {
            nutMapCache.remove(CACHE_PRE + clientId);
        }
    }

}
