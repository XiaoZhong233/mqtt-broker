package cn.mqtty.store.message;


import cn.mqtty.common.message.IMessageIdService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by wizzer on 2018
 */
@Component
@Slf4j
public class MessageIdService implements IMessageIdService {
    @Autowired
    private StringRedisTemplate redisService;

    @Override
    public int getNextMessageId() {
        try {
            while (true) {
                Long increment = redisService.opsForValue().increment("mqttwk:messageid:num");
                assert increment != null;
                int nextMsgId = (int) (increment.intValue() % 65536);
                if (nextMsgId > 0) {
                    return nextMsgId;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * 每次重启的时候初始化
     */
    @PostConstruct
    public void init() {
        redisService.delete("mqttwk:messageid:num");
    }
}
