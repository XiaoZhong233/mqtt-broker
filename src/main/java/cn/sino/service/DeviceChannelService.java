package cn.sino.service;

import io.netty.channel.ChannelHandlerContext;


public interface DeviceChannelService {

  boolean hasDevice(String channelId);

  String getSnByChannelId(String channelId);

  String getChannelIdBySn(String sn);

  void referenceChannelIdAndSn(String channelId, String sn);

  ChannelHandlerContext getChannelBySn(String sn);

  void removeChannelChannelId(String channelId);

  int deviceChannelCount();

}
