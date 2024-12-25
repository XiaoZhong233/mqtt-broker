package cn.mqtty.service.impl;

import cn.hutool.core.util.StrUtil;

import cn.mqtty.broker.handler.enums.ProtocolType;
import cn.mqtty.service.DeviceChannelService;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static cn.mqtty.broker.handler.OptionalSslHandler.PROTOCOL_TYPE_ATTRIBUTE_KEY;


@Slf4j
@Service
public class DeviceChannelServiceImpl implements DeviceChannelService {
    final BiMap<String, String> channelSnMap = Maps.synchronizedBiMap(HashBiMap.create());
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final Map<String, ChannelId> channelIdMap = new HashMap<>();
    @Bean(name = "channelGroup")
    public ChannelGroup getChannels() {
        return this.channelGroup;
    }

    @Bean(name = "channelIdMap")
    public Map<String, ChannelId> getChannelIdMap() {
        return this.channelIdMap;
    }

    @Override
    public String getSnByChannelId(String channelId) {
        return channelSnMap.get(channelId);
    }

    @Override
    public boolean isWebsocketChannel(Channel channel) {
        ProtocolType protocolType = channel.attr(PROTOCOL_TYPE_ATTRIBUTE_KEY).get();
        return protocolType==ProtocolType.WS;
    }
}
