package cn.mqtty.service.evt;

import cn.mqtty.service.evt.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.nio.channels.Channel;
import java.util.Set;

@Data
@AllArgsConstructor
public class WsActionEvt{
    String clientId;
    Set<String> topics;
    Action action;
}
