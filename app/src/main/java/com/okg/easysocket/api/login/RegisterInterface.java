package com.okg.easysocket.api.login;


import com.okg.easysocket.bean.ResponseMsg;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by oukanggui on 2018/3/9.
 */

public interface RegisterInterface {

    @GET("user/register")
    Call<ResponseMsg<String>> register(@Query("username") String username, @Query("password") String password,
                               @Query("code") String code);


}
