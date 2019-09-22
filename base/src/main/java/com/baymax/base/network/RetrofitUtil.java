package com.baymax.base.network;


import com.baymax.base.constant.Constants;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author oukanggui on 2018/3/9.
 */

public class RetrofitUtil {

    public static Retrofit createRetrofit() {
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//自定义OkHttpClient
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
//添加拦截器
        okHttpClient.addInterceptor(httpLoggingInterceptor);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL) // 设置 网络请求base url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .client(okHttpClient.build())
                .build();
        return retrofit;
    }

    /**
     * 把JsonObject转化为RequestBody对象，用户post请求创建RequestBody
     *
     * @param jsonObject
     * @return
     */
    public static RequestBody createRequestBody(JSONObject jsonObject) {
        return createRequestBody(jsonObject.toString());
    }

    /**
     * 把JsonString字符串转化为RequestBody对象，用户post请求创建RequestBody
     *
     * @param jsonStr
     * @return
     */
    public static RequestBody createRequestBody(String jsonStr) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        return body;
    }
}
