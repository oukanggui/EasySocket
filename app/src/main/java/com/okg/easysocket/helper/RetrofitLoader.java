package com.okg.easysocket.helper;

import androidx.annotation.NonNull;

import com.baymax.base.network.RetrofitHelper;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.constant.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Baymax
 * @date 2019-09-23
 * 描述：Retrofit加载器，对Retrofit进行进一步封装，减少样本代码
 */
public class RetrofitLoader {
    private static RetrofitLoader sRetrofitLoader;

    /**
     * Retrofit网络请求监听器
     * 其中泛型T值ResponseMsg里面data字段类型
     */
    public interface OnRequestListener<T> {
        /**
         * 创建Call对象
         *
         * @param retrofit
         * @return
         */
        Call<ResponseMsg<T>> onCreateCall(Retrofit retrofit);

        /**
         * 请求前操作
         */
        void onBefore();


        /**
         * 请求成功
         *
         * @param msg  提示信息
         * @param data 业务数据
         */
        void onSuccess(String msg, T data);

        /**
         * 请求失败
         *
         * @param errMsg  错误消息
         * @param errCode 错误码
         * @param t       Throwable
         */
        void onFail(String errMsg, int errCode, Throwable t);

    }

    private RetrofitLoader() {

    }

    public static RetrofitLoader getInstance() {
        if (sRetrofitLoader == null) {
            synchronized (RetrofitLoader.class) {
                if (sRetrofitLoader == null) {
                    sRetrofitLoader = new RetrofitLoader();
                }
            }
        }
        return sRetrofitLoader;
    }

    public <T> void request(@NonNull final OnRequestListener<T> onRequestListener) {
        onRequestListener.onBefore();
        Call<ResponseMsg<T>> call = onRequestListener.onCreateCall(RetrofitHelper.createRetrofit());
        if (call == null) {
            throw new NullPointerException("please create Call object with onCreateCall() before using");
        }
        call.enqueue(new Callback<ResponseMsg<T>>() {
            @Override
            public void onResponse(Call<ResponseMsg<T>> call, Response<ResponseMsg<T>> response) {
                ResponseMsg<T> responseMsg = response.body();
                if (responseMsg == null) {
                    onRequestListener.onFail("数据异常", response.code(), null);
                    return;
                }
                if (responseMsg.getCode() == AppConstants.CODE_SUCCESS) {
                    // 相应成功
                    onRequestListener.onSuccess(responseMsg.getMsg(), responseMsg.getData());
                } else {
                    onRequestListener.onFail(responseMsg.getMsg(), response.code(), null);
                }
            }

            @Override
            public void onFailure(Call<ResponseMsg<T>> call, Throwable t) {
                onRequestListener.onFail(AppConstants.ERROR_MSG, -1, t);
            }
        });
    }
}
