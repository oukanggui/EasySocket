package com.okg.easysocket.util;

/**
 * @author oukanggui
 * @date 2019/9/4
 * 描述：
 */
public class CommonUtil {
    /**
     * 点击事件，一秒内只能点击一次
     */
    private static final int CLICK_VALID_DELAY = 1000;
    private static long mLastClickedTime = 0;

    public static boolean isFastDoubleClick() {
        if ((System.currentTimeMillis() - mLastClickedTime) <= CLICK_VALID_DELAY) {
            return true;
        }
        mLastClickedTime = System.currentTimeMillis();
        return false;
    }

}
