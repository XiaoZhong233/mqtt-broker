## MQTT BROKER based on springboot and netty
### 项目结构
```
├─amqp 消息中间件相关支持，可自定义
├─broker BROKER相关支持
│  ├─codec 编解码器
│  ├─config 配置
│  ├─constants 常量类
│  ├─handler 
│  └─protocol MQTT协议相关
├─common 公共模块
│  ├─auth 授权相关
│  ├─message 消息相关
│  ├─session Session相关
│  └─subscribe 订阅相关
├─service 外部服务
│  ├─evt 封装一些事件
│  │  └─enums
│  ├─impl 
│  └─vo
├─store MQTT服务器会话信息
│  ├─cache
│  ├─message
│  ├─session
│  ├─subscribe
│  └─util
└─utils 公共工具包

```
