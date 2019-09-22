package com.okg.easysocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baymax.utilslib.SharedPreferencesUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.base.BaseAppTitleBarActivity;
import com.okg.easysocket.constant.AppConstants;

import butterknife.OnClick;

/**
 * @author Baymax
 * @date 2019-09-22
 * 描述：设置Activity
 */
public class SettingActivity extends BaseAppTitleBarActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setHeaderTitle("设置");
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.setting_about, R.id.setting_update, R.id.setting_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_about:
                ToastUtil.showToast(mContext, "功能规划中，敬请期待");
                break;
            case R.id.setting_update:
                ToastUtil.showToast(mContext, "当前已是最新版本");
                break;
            case R.id.setting_exit:
                //清空用户数据
                SharedPreferencesUtil.saveString(mContext, AppConstants.USER_ACCOUNT, "");
                SharedPreferencesUtil.saveString(mContext, AppConstants.USER_PASSWORD, "");
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }
}
