package cn.sino.service.impl;

import cn.hutool.core.util.StrUtil;

import cn.sino.service.DeviceChannelService;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.sino.broker.handler.ServerMqttHandler.clientMap;

@Slf4j
@Service
public class DeviceChannelServiceImpl implements DeviceChannelService {
    final BiMap<String, String> channelSnMap = Maps.synchronizedBiMap(HashBiMap.create());
    @Override
    public boolean hasDevice(String channelId) {
        return channelSnMap.containsKey(channelId);
    }

    @Override
    public String getSnByChannelId(String channelId) {
        return channelSnMap.get(channelId);
    }

    @Override
    public String getChannelIdBySn(String sn) {
        return channelSnMap.inverse().get(sn);
    }

    @Override
    public void referenceChannelIdAndSn(String channelId, String sn) {
        channelSnMap.put(channelId, sn);
    }

    @Override
    public ChannelHandlerContext getChannelBySn(String sn) {
        String channelId = channelSnMap.inverse().get(sn);
        if(StrUtil.isBlank(channelId)){
            return null;
        }
        return clientMap.get(channelId);
    }

    @Override
    public void removeChannelChannelId(String channelId) {
        channelSnMap.remove(channelId);
    }

    @Override
    public int deviceChannelCount() {
        return channelSnMap.size();
    }
}
