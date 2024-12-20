package cn.mqtty.broker.handler;
import cn.mqtty.broker.handler.enums.SslStatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class OptionalSslHandler extends io.netty.handler.ssl.OptionalSslHandler {

    public static final AttributeKey<SslStatus> SSL_STATUS = AttributeKey.valueOf("SSL_STATUS");

    public OptionalSslHandler(SslContext sslContext) {
        super(sslContext);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        ctx.channel().attr(SSL_STATUS).set(null);
    }


    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        super.decode(context, in, out);
        if (in.readableBytes() >= 5) {
            if (SslHandler.isEncrypted(in)) {
                context.channel().attr(SSL_STATUS).set(SslStatus.ENABLED);
                log.info("channel[{}] enable ssl", context.channel().id());
            } else {
                context.channel().attr(SSL_STATUS).set(SslStatus.DISABLED);
                log.info("channel[{}] doesn't enable ssl", context.channel().id());
            }
        }
    }

}
