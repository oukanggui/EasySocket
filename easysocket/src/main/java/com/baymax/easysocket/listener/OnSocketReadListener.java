package com.baymax.easysocket.listener;

/**
 * @author oukanggui
 * @date 2019/8/28
 * 描述：读到服务器数据时监听器
 */
public interface OnSocketReadListener {
    /**
     * 接收到心跳数据回调
     *
     * @param message
     */
    void onReceiveHeartBeatData(String message);

    /**
     * 接收到业务数据回调
     *
     * @param message
     */
    void onReceiveBusinessData(String message);
}
