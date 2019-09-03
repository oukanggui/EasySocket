package com.baymax.easysocket.listener;

/**
 * @author oukanggui
 * @date 2019/8/26
 * 描述：Socket发送消息状态监听器
 */
public interface OnSocketRequestListener {
    /**
     * 发送成功
     */
    void onSuccess();

    /**
     * 发送失败
     *
     * @param reason 失败原因
     */
    void onFailure(String reason);
}
