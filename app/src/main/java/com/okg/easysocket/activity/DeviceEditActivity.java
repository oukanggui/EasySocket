package com.okg.easysocket.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.baymax.base.network.RetrofitLoader;
import com.baymax.utilslib.TextUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.api.device.DeviceInterface;
import com.okg.easysocket.base.BaseAppTitleBarActivity;
import com.okg.easysocket.bean.DeviceInfo;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.manager.UserInfoManager;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceEditActivity extends BaseAppTitleBarActivity {

    public static String KEY_DEVICE_INFO;
    @BindView(R.id.add_et_device_name)
    EditText etDeviceName;
    @BindView(R.id.add_et_device_number)
    EditText etDeviceSerialNumber;
    private DeviceInfo mDeviceInfo;
    private boolean isEditMode = false;


    @Override
    public int getLayoutId() {
        return R.layout.activity_device_edit;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mDeviceInfo = (DeviceInfo) getIntent().getSerializableExtra(KEY_DEVICE_INFO);
        isEditMode = mDeviceInfo == null;
        if (mDeviceInfo == null) {
            isEditMode = false;
            setHeaderTitle("添加设备");
        } else {
            isEditMode = true;
            setHeaderTitle("编辑设备");
            etDeviceName.setText(mDeviceInfo.getDeviceName());
            etDeviceSerialNumber.setText(mDeviceInfo.getDeviceSn());
            etDeviceSerialNumber.setEnabled(false);
        }

    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.add_ensure)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_ensure:
                String strDeviceName = etDeviceName.getText().toString().trim();
                String strDeviceSerialNumber = etDeviceSerialNumber.getText().toString().trim();
                if (TextUtil.isEmpty(strDeviceName)) {
                    ToastUtil.showToast(mContext, "设备名称不能为空");
                    break;
                }
                if (TextUtil.isEmpty(strDeviceSerialNumber)) {
                    ToastUtil.showToast(mContext, "设备序列号不能为空");
                    break;
                }
                showLoadingDialog("处理中");
                Retrofit retrofit = RetrofitLoader.createRetrofit();
                Call<ResponseMsg<String>> call;
                if (isEditMode) {
                    call = retrofit.create(DeviceInterface.class).saveDevice(mDeviceInfo.getDeviceId(), strDeviceName);
                } else {
                    String userAccount = UserInfoManager.getInstance(mContext).getUserAccount();
                    call = retrofit.create(DeviceInterface.class).addDevice(userAccount, strDeviceName, strDeviceSerialNumber);
                }
                call.enqueue(new Callback<ResponseMsg<String>>() {
                    @Override
                    public void onResponse(Call<ResponseMsg<String>> call, Response<ResponseMsg<String>> response) {
                        dismissLoadingDialog();
                        ResponseMsg<String> responseMsg = response.body();
                        if (responseMsg == null) {
                            ToastUtil.showToast(mContext, "操作异常，请稍后重试");
                            return;
                        }
                        ToastUtil.showToast(mContext, responseMsg.getMsg());
                        if (responseMsg.getCode() == 1) {
                            // 添加成功后退出
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMsg<String>> call, Throwable t) {
                        dismissLoadingDialog();
                        ToastUtil.showToast(mContext, "操作失败，请稍后重试");
                    }
                });
                break;
            default:
                break;
        }
    }
}
