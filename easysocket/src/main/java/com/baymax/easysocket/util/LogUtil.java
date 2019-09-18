package com.baymax.easysocket.util;

/**
 * Author:oukanggui
 * Date: 2019-09-17
 * Describe:Log工具类
 */
public class LogUtil {
    private static String TAG = "EasySocket";

    /**
     * 输出Console log
     *
     * @param log
     */
    public static void printlnLog(String log) {
        System.out.println(TAG + " : " + log);
    }
}
