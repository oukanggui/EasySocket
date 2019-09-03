package com.baymax.easysocket.util;

import android.util.Log;

import com.baymax.easysocket.bean.SocketBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author oukanggui
 * @date 2019/9/3
 * 描述：Json工具类
 */
public class JsonUtil {
    private static final String TAG = JsonUtil.class.getSimpleName();

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_DEVICE_ID = "deviceId";
    public static final String FIELD_DATA = "data";

    /**
     * Socket对象转化为Json String字符串
     *
     * @return
     */
    public static String convertSocketBeanToJson(SocketBean socketBean) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(FIELD_TYPE, socketBean.type);
            jsonObject.put(FIELD_DEVICE_ID, socketBean.deviceId);
            jsonObject.put(FIELD_DATA, socketBean.data);
        } catch (JSONException e) {
            Log.e(TAG, "convertSocketBeanToJson error = " + e);
        }
        return jsonObject.toString();
    }

    /**
     * Json String字符串转化为Socket对象
     *
     * @return
     */
    public static SocketBean parseJsonToSocketBean(String jsonString) {
        SocketBean socketBean = new SocketBean();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            socketBean.type = jsonObject.getInt(FIELD_TYPE);
            socketBean.deviceId = jsonObject.getString(FIELD_DEVICE_ID);
            socketBean.data = jsonObject.getString(FIELD_DATA);
        } catch (JSONException e) {
            Log.e(TAG, "parseJsonToSocketBean error = " + e);
        }
        return socketBean;
    }
}
