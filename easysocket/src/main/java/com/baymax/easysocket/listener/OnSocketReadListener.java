package com.baymax.easysocket.listener;

import com.baymax.easysocket.bean.SocketBean;

/**
 * @author oukanggui
 * @date 2019/8/28
 * 描述：读到服务器数据时监听器
 */
public interface OnSocketReadListener {
    /**
     * 接收到心跳数据回调
     *
     * @param socketBean
     */
    void onReceiveHeartBeatData(SocketBean socketBean);

    /**
     * 接收到业务数据回调
     *
     * @param socketBean
     */
    void onReceiveBusinessData(SocketBean socketBean);
}
