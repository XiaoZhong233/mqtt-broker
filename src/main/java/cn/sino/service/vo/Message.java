package cn.sino.service.vo;

import lombok.Data;

@Data
public class Message<T> {
    String sn;
    String type;
    T msg;
    Long timestamp;
}
