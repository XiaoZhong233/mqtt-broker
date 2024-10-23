package cn.mqtty.broker;

import cn.mqtty.broker.config.Cert;
import cn.mqtty.broker.handler.ServerMqttHandler;
import io.netty.handler.ssl.SslContext;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class MqttServerRunner implements CommandLineRunner {


    @Autowired
    ServerMqttHandler serverMqttHandler;
//    @Autowired
//    IdleReadStateHandler idleReadStateHandler;

    private SslContext sslContext;

    @Value("${mqtt.enable_ssl}")
    private Boolean sslEnabled;


    @Value("${mqtt.port}")
    private Integer port;


    @PostConstruct
    public void init() throws IOException {
        // Single way authentication
        sslContext = Cert.build();
    }

    @Override
    public void run(String... args) throws Exception {
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        public void initChannel(SocketChannel ch) {
//                            if (sslEnabled) {
//                                ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
//                            }
//                            ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(2,0,0, TimeUnit.MINUTES))
//                                    .addLast(idleReadStateHandler);
//                            ch.pipeline().addLast("mqttDecoder", new MqttDecoder(8192));
//                            ch.pipeline().addLast("mqttEncoder", MqttEncoder.INSTANCE);
//                            ch.pipeline().addLast("mqttHandler", serverMqttHandler);
//                        }
//                    })
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//
//            // Bind and start to accept incoming connections.
//            b.bind(port).sync().channel().closeFuture().sync();
//        } finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
//        }
    }
}
