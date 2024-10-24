package cn.mqtty.broker.msg;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ActionMsg {
    String sn;
    String type;
    JsonNode msg;
    Long timestamp;
}
