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
    public static final String FIELD_FROM = "from";
    public static final String FIELD_STATUS = "status";
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
            jsonObject.put(FIELD_FROM, socketBean.from);
            jsonObject.put(FIELD_STATUS, socketBean.status);
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
            socketBean.type = jsonObject.optInt(FIELD_TYPE);
            socketBean.from = jsonObject.optInt(FIELD_FROM);
            socketBean.status = jsonObject.optInt(FIELD_STATUS);
            socketBean.deviceId = jsonObject.optString(FIELD_DEVICE_ID);
            socketBean.data = jsonObject.optString(FIELD_DATA);
        } catch (JSONException e) {
            Log.e(TAG, "parseJsonToSocketBean error = " + e);
        }
        return socketBean;
    }
}
