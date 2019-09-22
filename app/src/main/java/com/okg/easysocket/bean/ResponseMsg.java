package com.okg.easysocket.bean;

import com.okg.easysocket.proguard.IProguard;

public class ResponseMsg<T> implements IProguard {

    /**
     * 状态码
     */
    private int code;
    /**
     * 状态消息
     */
    private String msg;
    /**
     * 业务数据
     */
    private T data;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
