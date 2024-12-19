/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.amqp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 内部消息
 */
@Data
@AllArgsConstructor
public class InternalMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    private String sn;

    private String clientId;

    private String topic;

    private int mqttQoS;

    private byte[] messageBytes;

}
