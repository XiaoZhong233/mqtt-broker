package cn.mqtty;

import cn.mqtty.broker.BrokerServer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BrokerRunner implements CommandLineRunner {

    private final BrokerServer brokerServer;

    public BrokerRunner(BrokerServer brokerServer){
        this.brokerServer = brokerServer;
    }

    @Override
    public void run(String... args) throws Exception {
        brokerServer.start();
    }

    @PreDestroy
    public void onExit() {
        brokerServer.stop(); // 停止服务
    }
}
