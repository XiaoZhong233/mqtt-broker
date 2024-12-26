package cn.mqtty.service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command<T> {
    String sn;
    String type;
    T msg;
    String id;
    Long timestamp;
}
