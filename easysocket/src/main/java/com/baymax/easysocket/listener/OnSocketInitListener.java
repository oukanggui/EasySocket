package com.baymax.easysocket.listener;

/**
 * @author oukanggui
 * @date 2019/9/4
 * 描述：Socket初始化监听器
 */
public interface OnSocketInitListener {
    /**
     * Socket初始化成功
     *
     * @param message
     */
    void onSocketInitSuccess(String message);

    /**
     * Socket初始化失败
     *
     * @param reason 失败原因
     */
    void onSocketInitFailure(String reason);
}
