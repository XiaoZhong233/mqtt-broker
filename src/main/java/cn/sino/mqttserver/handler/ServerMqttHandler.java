package cn.sino.mqttserver.handler;

import cn.hutool.core.collection.CollUtil;
import cn.sino.mqttserver.proto.MqttMsgBack;
import cn.sino.service.DeviceChannelService;
import cn.sino.service.impl.DeviceChannelServiceImpl;
import cn.sino.service.impl.MqttLoggerService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerMqttHandler extends SimpleChannelInboundHandler<MqttMessage> {

    public static final ConcurrentHashMap<String, ChannelHandlerContext> clientMap = new ConcurrentHashMap<>();

//    @Value("${driver.mqtt.address_list}")
//    private List<String> addressList;

    @Autowired
    private MqttMsgBack mqttMsgBack;
    @Autowired
    DeviceChannelService deviceChannelService;
    @Autowired
    MqttLoggerService loggerService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        //判断连接是否合法
        clientMap.put(ctx.channel().id().toString(), ctx);
//        log.info("设备连接： {}", address.getAddress());
        super.handlerAdded(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        if(mqttMessage!=null){
            MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
            switch (mqttFixedHeader.messageType()){
                // ----------------------接收消息端（服务端）可能会触发的事件----------------------------------------------------------------
                case CONNECT:
                    //	在一个网络连接上，客户端只能发送一次CONNECT报文。服务端必须将客户端发送的第二个CONNECT报文当作协议违规处理并断开客户端的连接
                    //	建议connect消息单独处理，用来对客户端进行认证管理等 这里直接返回一个CONNACK消息
                    mqttMsgBack.connectionAck(ctx, mqttMessage);
                    break;
                case PUBLISH:
                    //	收到消息，返回确认，PUBACK报文是对QoS 1等级的PUBLISH报文的响应,PUBREC报文是对PUBLISH报文的响应
                    mqttMsgBack.publishAck(ctx, mqttMessage);
                    break;
                case PUBREL:
                    //	释放消息，PUBREL报文是对QoS 2等级的PUBREC报文的响应,此时我们应该回应一个PUBCOMP报文
                    mqttMsgBack.publishComp(ctx, mqttMessage);
                    break;
                case SUBSCRIBE:
                    //	客户端订阅主题
                    //	客户端向服务端发送SUBSCRIBE报文用于创建一个或多个订阅，每个订阅注册客户端关心的一个或多个主题。
                    //	为了将应用消息转发给与那些订阅匹配的主题，服务端发送PUBLISH报文给客户端。
                    //	SUBSCRIBE报文也（为每个订阅）指定了最大的QoS等级，服务端根据这个发送应用消息给客户端
                    mqttMsgBack.subscribeAck(ctx, mqttMessage);
                    break;
                case UNSUBSCRIBE:
                    //	客户端取消订阅
                    //	客户端发送UNSUBSCRIBE报文给服务端，用于取消订阅主题
                    mqttMsgBack.unsubscribeAck(ctx, mqttMessage);
                    break;
                case PINGREQ:
                    //	客户端发起心跳
                    mqttMsgBack.pingResp(ctx, mqttMessage);
                    break;
                case DISCONNECT:
                    //	客户端主动断开连接
                    //	DISCONNECT报文是客户端发给服务端的最后一个控制报文， 服务端必须验证所有的保留位都被设置为0
                    mqttMsgBack.handleDisconnect(ctx);
                    break;
                // ----------------------服务端作为发送消息端可能会接收的事件----------------------------------------------------------------
                case PUBACK:
                case PUBREC:
                    //QoS 2级别,响应一个PUBREL报文消息，PUBACK、PUBREC这俩都是ack消息
                    //PUBACK报文是对QoS 1等级的PUBLISH报文的响应，如果一段时间没有收到客户端ack，服务端会重新发送消息
                    mqttMsgBack.receivePubAck(ctx, mqttMessage);
                    break;
                case PUBCOMP:
                    //收到qos2级别接收端最后一次发送过来的确认消息
                    mqttMsgBack.receivePubcomp(ctx, mqttMessage);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        delSubCache(ctx);
        loggerService.logInactive(ctx.channel().id().toString());
        deviceChannelService.removeChannelChannelId(ctx.channel().id().toString());
        super.channelInactive(ctx);
    }


    private void delSubCache(ChannelHandlerContext ctx){
        String id = ctx.channel().id().toString();
        clientMap.remove(id);
        //删除订阅主题
        Set<String> topicSet = MqttMsgBack.ctMap.get(id);
        if (CollUtil.isNotEmpty(topicSet)) {
            ConcurrentHashMap<String, HashSet<String>> subMap = MqttMsgBack.subMap;
            ConcurrentHashMap<String, MqttQoS> qoSMap = MqttMsgBack.qoSMap;
            for (String topic : topicSet) {
                if (subMap != null) {
                    HashSet<String> ids = subMap.get(topic);
                    if (CollUtil.isNotEmpty(ids)) {
                        ids.remove(id);
                        if (CollUtil.isEmpty(ids)) {
                            subMap.remove(topic);
                        }
                    }
                }
                if (qoSMap != null) {
                    qoSMap.remove(topic + "-" + id);
                }
            }
        }
        MqttMsgBack.ctMap.remove(id);
    }
}
