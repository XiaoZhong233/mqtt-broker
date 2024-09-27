package cn.sino.broker;

import cn.sino.broker.config.Cert;
import cn.sino.broker.handler.IdleReadStateHandler;
import cn.sino.broker.handler.ServerMqttHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
@Slf4j
public class MqttServerRunner implements CommandLineRunner {


    @Autowired
    ServerMqttHandler serverMqttHandler;
    @Autowired
    IdleReadStateHandler idleReadStateHandler;

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
