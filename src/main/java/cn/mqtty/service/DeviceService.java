package cn.mqtty.service;

import io.netty.channel.Channel;

public interface DeviceService {

    void online(Channel channel, String clientId);

    void offline(Channel channel, String clientId);
}
