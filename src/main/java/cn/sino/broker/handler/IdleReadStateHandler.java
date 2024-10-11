package cn.sino.broker.handler;

import cn.sino.broker.proto.MqttMsgBack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
//@Component
@ChannelHandler.Sharable
public class IdleReadStateHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                //断开连接
                Channel channel = ctx.channel();
                String channelId = channel.id().asShortText();
                log.info("channel {} is read idle, close it", channelId);
                channel.close();
            }
        }
    }
}
