package cn.mqtty.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;


public interface DeviceChannelService {
  String getSnByChannelId(String channelId);

  boolean isWebsocketChannel(Channel channel);
}
