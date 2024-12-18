package cn.mqtty.broker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mqtt")
@Data
public class MqttConfig {
    private String username;
    private String password;
    private int waitTime;
    private int interval;
    private int port;
    private boolean enableCleanSession;
    private boolean enableSsl;
    private String caCertFile;
    private String serverCertFile;
    private String keyFile;
}
