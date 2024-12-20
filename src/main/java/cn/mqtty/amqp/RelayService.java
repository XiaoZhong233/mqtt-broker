package cn.mqtty.amqp;

import cn.mqtty.broker.config.BrokerProperties;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.nutz.lang.Lang;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RelayService {

    DefaultMQProducer producer;
    BrokerProperties brokerProperties;
    RocketMqConfig rocketMqConfig;

    final static String ROCKET_ACTION_TOPIC = "mqtt_relay";
    final static String ROCKET_ACTION_TAG = "terminal";
    final static String ROCKET_LOG_TAG = "log";

    public void send(InternalMessage internalMessage) {
//        String processId = Lang.JdkTool.getProcessId("0");
        //broker唯一标识 mqttwk.broker.id
//        internalMessage.setBrokerId(brokerProperties.getId());
//        internalMessage.setProcessId(processId);
        if(brokerProperties.isAmqp_enable()){
            log.info("设备{}AMPQ转发数据：topic:{}, 长度:{}", internalMessage.getSn(), internalMessage.getTopic(),
                    internalMessage.getMessageBytes().length);
            if(internalMessage.getTopic().startsWith("$remote/client2server")){
                Message msg = new Message(ROCKET_ACTION_TOPIC,
                        ROCKET_ACTION_TAG,
                        JSON.toJSONString(internalMessage).getBytes());
                try {
                    producer.sendOneway(msg);
                }catch (Exception e){
                    log.error("Rocket MQ connect loss...", e);
                }
            }
            if(internalMessage.getTopic().startsWith("$log/operation")){
                Message msg = new Message(ROCKET_ACTION_TOPIC,
                        ROCKET_LOG_TAG,
                        JSON.toJSONString(internalMessage).getBytes());
                try {
                    producer.sendOneway(msg);
                }catch (Exception e){
                    log.error("Rocket MQ connect loss...", e);
                }
            }
        }
    }
}
