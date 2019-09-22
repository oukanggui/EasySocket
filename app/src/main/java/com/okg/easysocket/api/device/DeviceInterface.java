package com.okg.easysocket.api.device;

import com.okg.easysocket.bean.DeviceInfo;
import com.okg.easysocket.bean.ResponseMsg;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Baymax
 * @date 2019-09-22
 * 描述：
 */
public interface DeviceInterface {
    /**
     * 获取用户设备列表
     *
     * @param userName
     * @return
     */
    @GET("device/list")
    Call<ResponseMsg<List<DeviceInfo>>> getDeviceList(@Query("userName") String userName);

    /**
     * 添加设备
     */
    @GET("device/add")
    Call<ResponseMsg<String>> addDevice(@Query("userName") String userName,
                                        @Query("deviceName") String deviceName,
                                        @Query("deviceSn") String deviceSn);

    /**
     * 编辑设备
     */
    @GET("device/edit")
    Call<ResponseMsg<String>> saveDevice(@Query("deviceId") int deviceId,
                                         @Query("deviceName") String deviceName);

    /**
     * 删除设备
     */
    @GET("device/delete")
    Call<ResponseMsg<String>> deleteDevice(@Query("deviceId") int deviceId);
}
