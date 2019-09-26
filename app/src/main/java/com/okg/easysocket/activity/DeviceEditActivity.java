package com.okg.easysocket.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.baymax.utilslib.TextUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.api.device.DeviceInterface;
import com.okg.easysocket.base.BaseAppTitleBarActivity;
import com.okg.easysocket.bean.DeviceInfo;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.helper.RetrofitLoader;
import com.okg.easysocket.manager.UserInfoManager;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
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
                final String strDeviceName = etDeviceName.getText().toString().trim();
                final String strDeviceSerialNumber = etDeviceSerialNumber.getText().toString().trim();
                if (TextUtil.isEmpty(strDeviceName)) {
                    ToastUtil.showToast(mContext, "设备名称不能为空");
                    break;
                }
                if (TextUtil.isEmpty(strDeviceSerialNumber)) {
                    ToastUtil.showToast(mContext, "设备序列号不能为空");
                    break;
                }

                RetrofitLoader.getInstance().request(new RetrofitLoader.OnRequestListener<String>() {
                    @Override
                    public void onBefore() {
                        showLoadingDialog("提交中");
                    }

                    @Override
                    public Call<ResponseMsg<String>> onCreateCall(Retrofit retrofit) {
                        if (isEditMode) {
                            return retrofit.create(DeviceInterface.class).saveDevice(mDeviceInfo.getDeviceId(), strDeviceName);
                        }
                        String userAccount = UserInfoManager.getInstance(mContext).getUserAccount();
                        return retrofit.create(DeviceInterface.class).addDevice(userAccount, strDeviceName, strDeviceSerialNumber);
                    }

                    @Override
                    public void onSuccess(String msg, String data) {
                        dismissLoadingDialog();
                        ToastUtil.showToast(mContext, msg);
                        finish();
                    }

                    @Override
                    public void onFail(String errMsg, int errCode, Throwable t) {
                        dismissLoadingDialog();
                        ToastUtil.showToast(mContext, errMsg);
                    }
                });
                break;
            default:
                break;
        }
    }
}
