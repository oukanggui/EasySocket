package com.okg.easysocket.api.login;


import com.okg.easysocket.bean.ResponseMsg;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by oukanggui on 2018/3/10.
 * 登录接口
 */

public interface LoginInterface {
    @GET("user/login")
    Call<ResponseMsg> login(@Query("username") String username, @Query("password") String password);
}
