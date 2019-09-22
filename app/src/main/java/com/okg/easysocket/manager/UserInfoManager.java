package com.okg.easysocket.manager;

import android.content.Context;

import com.baymax.utilslib.SharedPreferencesUtil;
import com.okg.easysocket.constant.AppConstants;

/**
 * @author Baymax
 * @date 2019-09-22
 * 描述：用户信息管理器
 */
public class UserInfoManager {
    private static UserInfoManager sUserInfoManager;
    private static Context mContext;

    private UserInfoManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static UserInfoManager getInstance(Context context) {
        if (sUserInfoManager == null) {
            synchronized (UserInfoManager.class) {
                if (sUserInfoManager == null) {
                    sUserInfoManager = new UserInfoManager(context);
                }
            }
        }
        return sUserInfoManager;
    }

    /**
     * 保存用户信息
     *
     * @param account
     * @param password
     */
    public void saveUserInfo(String account, String password) {
        SharedPreferencesUtil.saveString(mContext, AppConstants.USER_ACCOUNT, account);
        SharedPreferencesUtil.saveString(mContext, AppConstants.USER_PASSWORD, password);
    }

    /**
     * 清空用户信息
     */
    public void clearUserInfo() {
        SharedPreferencesUtil.saveString(mContext, AppConstants.USER_ACCOUNT, "");
        SharedPreferencesUtil.saveString(mContext, AppConstants.USER_PASSWORD, "");
    }

    /**
     * 获取用户账号信息
     *
     * @return
     */
    public String getUserAccount() {
        return SharedPreferencesUtil.getString(mContext, AppConstants.USER_ACCOUNT, null);
    }

    /**
     * 获取用户密码信息
     *
     * @return
     */
    public String getUserPassword() {
        return SharedPreferencesUtil.getString(mContext, AppConstants.USER_PASSWORD, null);
    }
}
