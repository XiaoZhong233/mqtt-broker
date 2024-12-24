package cn.mqtty.broker.handler;

import cn.mqtty.broker.handler.enums.ProtocolType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import static cn.mqtty.broker.handler.OptionalSslHandler.PROTOCOL_TYPE_ATTRIBUTE_KEY;

@Slf4j
@ChannelHandler.Sharable
public class MqttWsCustomHandler extends ChannelInboundHandlerAdapter {

//    public static final AttributeKey<ProtocolType> PROTOCOL_TYPE_ATTRIBUTE_KEY = AttributeKey.valueOf("PROTOCOL_TYPE");

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        ctx.channel().attr(PROTOCOL_TYPE_ATTRIBUTE_KEY).set(ProtocolType.WS);
    }
}
