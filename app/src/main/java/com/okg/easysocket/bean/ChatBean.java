package com.okg.easysocket.bean;

import com.okg.easysocket.proguard.IProguard;

/**
 * @author oukanggui
 * @date 2019/9/4
 * 描述：聊天数据消息
 */
public class ChatBean implements IProguard {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;

    public ChatBean(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
