package com.baymax.easysocket.listener;


import com.baymax.easysocket.bean.SocketBean;

/**
 * @author oukanggui
 * @date 2019/8/27
 * 描述：Socket消息响应监听器，当客户端收到服务器Socket的非心跳消息时回调
 */
public interface OnSocketResponseListener {
    /**
     * 在主线程执行
     *
     * @param socketBean 服务器返回数据的通用数据体
     */
    void onResponse(SocketBean socketBean);
}
