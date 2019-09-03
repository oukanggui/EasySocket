package com.baymax.easysocket.bean;


/**
 * @author oukanggui
 * @date 2019/8/27
 * 描述：Socket通信传输的数据实体，需区分心跳数据和业务数据，采用json形式传输
 */
public class SocketBean {
    /**
     * 传输数据实体类型
     * 0：心跳数据包
     * 非0：业务数据包，具体业务类型需要自己定义
     */
    public int type;

    /**
     * 设备唯一身份标识Id，用于指定通信的设备
     */
    public String deviceId;

    /**
     * 发送给服务器的数据，如果为心跳数据包，可以为空，具体解析有客户端业务层去解析处理
     */
    public String data;

    /**
     * 判断是否是心跳数据
     * @return
     */
    public boolean isHeartBeatData() {
        return this.type == 0;
    }
}
