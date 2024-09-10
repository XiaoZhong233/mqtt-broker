package cn.sino.mqttserver.proto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Subscription {
    private String topic;
    private int qos; // QoS级别
    @Override
    public String toString() {
        return "Subscription{" +
                "topic='" + topic + '\'' +
                ", qos=" + qos +
                '}';
    }
}