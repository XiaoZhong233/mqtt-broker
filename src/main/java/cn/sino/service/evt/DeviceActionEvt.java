package cn.sino.service.evt;


import cn.sino.service.evt.enums.Action;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceActionEvt {
    String sn;
    Channel channel;
    Action action;
}
