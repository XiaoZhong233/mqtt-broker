package cn.mqtty.service;

import io.netty.channel.Channel;

public interface DeviceService {

    void online(Channel channel, String sn);

    void offline(Channel channel, String sn);
}
