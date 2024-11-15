package cn.mqtty.broker.handler;

import cn.mqtty.broker.protocol.ProtocolProcess;
import cn.mqtty.common.session.SessionStore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@ChannelHandler.Sharable
@Component
public class IdleReadStateHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ProtocolProcess protocolProcess;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.ALL_IDLE){
                Channel channel = ctx.channel();
                String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
                // 发送遗嘱消息
                if (this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
                    SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
                    if (sessionStore.getWillMessage() != null) {
                        log.info("客户端{}发送遗嘱消息", clientId);
                        this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
                    }
                }
                //断开连接
                String channelId = channel.id().asShortText();
                log.info("客户端{}触发空闲事件...即将关闭", clientId);
                channel.close();
            }
        }
    }
}
