package cn.sino;

import cn.sino.broker.BrokerServer;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
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
