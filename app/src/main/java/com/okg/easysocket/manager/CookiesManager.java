package com.okg.easysocket.manager;

import com.baymax.utilslib.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @author Baymax
 * @date 2019-09-23
 * 描述：Cookies管理器
 * 注册时获取验证码接口服务器需要获取Cookies
 */
public class CookiesManager implements CookieJar {

    /**
     * 保存每个url的cookie
     */
    private HashMap<HttpUrl, List<Cookie>> mCookieStoreMap = new HashMap<>();

    /**
     * 上一个请求url
     */
    private HttpUrl mLastHttpUrl;

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        //保存链接的cookie
        mCookieStoreMap.put(httpUrl, list);
        //保存上一次的url，供给下一次cookie的提取
        mLastHttpUrl = httpUrl;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        //加载上一个链接的cookie
        List<Cookie> cookies = mCookieStoreMap.get(mLastHttpUrl);
        return cookies != null ? cookies : new ArrayList<Cookie>();

    }
}