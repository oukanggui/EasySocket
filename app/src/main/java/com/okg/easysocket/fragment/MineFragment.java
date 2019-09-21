package com.okg.easysocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baymax.utilslib.SharedPreferencesUtil;
import com.baymax.utilslib.TextUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.activity.LoginActivity;
import com.okg.easysocket.base.BaseAppFragment;
import com.okg.easysocket.constant.AppConstants;
import com.baymax.base.widget.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author oukanggui on 2018/2/2.
 */

public class MineFragment extends BaseAppFragment {

    @BindView(R.id.mine_nickname)
    TextView tvNickName;

    @BindView(R.id.mine_addition)
    TextView tvAddition;

    @BindView(R.id.mine_iv_head)
    CircleImageView ivHeadPic;


    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    protected void loadData() {

    }

    @OnClick({R.id.mine_user_info, R.id.mine_about, R.id.mine_device, R.id.mine_setting, R.id.mine_exit})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.mine_user_info:
            case R.id.mine_about:
            case R.id.mine_device:
            case R.id.mine_setting:
                ToastUtil.showToast(mActivity, "功能规划中，敬请期待");
                break;
            case R.id.mine_exit:
                intent = new Intent(mActivity, LoginActivity.class);
                //清空用戶數據
                SharedPreferencesUtil.saveString(mActivity, AppConstants.USER_ACCOUNT, "");
                SharedPreferencesUtil.saveString(mActivity, AppConstants.USER_PASSWORD, "");
                startActivity(intent);
                mActivity.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            String nickName = SharedPreferencesUtil.getString(mActivity, AppConstants.USER_ACCOUNT, "");
            if (TextUtil.isEmpty(nickName)) {
                nickName = "未知用户";
            }
            tvNickName.setText(nickName);
        }
    }
}
