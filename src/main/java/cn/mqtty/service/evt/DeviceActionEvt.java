package cn.mqtty.service.evt;


import cn.mqtty.service.evt.enums.Action;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceActionEvt {
    String clientId;
    String sn;
    Channel channel;
    Action action;
}
