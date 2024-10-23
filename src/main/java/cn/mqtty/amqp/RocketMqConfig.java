package cn.mqtty.amqp;


import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Getter
public class RocketMqConfig {
    private final String nameServer;
    private final String producerGroup;
    private final String relayTopic;
    private final Integer retryTime;
    private DefaultMQProducer producer;


    public RocketMqConfig(@Value("${rocketmq.name-server}") String nameServer,
                          @Value("${rocketmq.producer.group}")String producerGroup,
                          @Value("${rocketmq.producer.relay.topic}")String relayTopic,
                          @Value("${rocketmq.producer.relay.retries}")Integer retries){
        this.nameServer = nameServer;
        this.producerGroup = producerGroup;
        this.relayTopic = relayTopic;
        this.retryTime = retries;
    }

    @Bean("RocketMqTemplate")
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup); //（1）
        producer.setNamesrvAddr(nameServer);
        this.producer = producer;
        producer.start();
        return producer;
    }

    @PreDestroy
    public void shutdownProducer() {
        if (producer != null) {
            producer.shutdown();
            log.info("RocketMQ Producer shutdown successfully.");
        }
    }
}
