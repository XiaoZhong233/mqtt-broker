package cn.mqtty.broker.config;

import cn.hutool.core.io.resource.ResourceUtil;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

//密钥生成参考：https://www.cnblogs.com/exmyth/p/14808872.html
@Slf4j
@Component
public class Cert {

    @Autowired
    private MqttConfig mqttConfig;

    private static String caCertFile;

    private static String serverCertFile;

    private static String keyFile;

    @PostConstruct
    public void init() {
        caCertFile = mqttConfig.getCaCertFile();
        serverCertFile = mqttConfig.getServerCertFile();
        keyFile = mqttConfig.getKeyFile();
    }

    public static SslContext build() throws IOException {
        InputStream certInput = null;
        InputStream priKeyInput = null;
        InputStream caInput = null;
        try {
            certInput = ResourceUtil.getStream(serverCertFile);
            priKeyInput = ResourceUtil.getStream(keyFile);
            caInput = ResourceUtil.getStream(caCertFile);
            return SslContextBuilder.forServer(certInput, priKeyInput)
                    .clientAuth(ClientAuth.REQUIRE)
                    .trustManager(caInput)
                    .build();
        } catch (Throwable e) {
            log.error("SslContext build error", e);
        } finally {
            assert certInput != null;
            certInput.close();
            assert priKeyInput != null;
            priKeyInput.close();
            assert caInput != null;
            caInput.close();
        }
        return null;
    }

    public static SslContext buildSelfSignedCer() {
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .build();
        } catch (Throwable e) {
            log.error("buildSelfSignedCer", e);
        }
        return null;
    }
}
