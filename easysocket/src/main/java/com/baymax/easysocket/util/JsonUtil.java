package com.baymax.easysocket.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author oukanggui
 * @date 2019/9/3
 * 描述：Json工具类
 */
public class JsonUtil {
    public static Gson gson = new Gson();

    public static <T> T parse(String response, Class<T> valueType) {
        if (StringUtil.isEmpty(response) || valueType == null) {
            return null;
        }
        try {
            if (gson == null) {
                gson = new Gson();
            }
            return gson.fromJson(response, valueType);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.printlnLog("Gson parse Exception " + e.getMessage());
        }
        return null;
    }

    public static <T> List<T> parse(String response, Type listType) {
        if (StringUtil.isEmpty(response) || listType == null) {
            return null;
        }
        if (gson == null) {
            gson = new Gson();
        }
        try {
            return gson.fromJson(response, listType);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.printlnLog("Gson parse Exception " + e.getMessage());
        }
        return null;
    }

    public static String toJson(Object o) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.toJson(o);
    }
}
