package com.baymax.base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.baymax.base.widget.LoadingDialog;
import com.baymax.base.widget.StateView;

/**
 * @author oukanggui on 2018/1/28.
 * 使用StateView注入加载动画、加载失败、加载无数据
 */

public abstract class BaseActivity extends FragmentActivity {
    public String TAG;
    public Context mContext;
    public StateView mStateView;
    protected LoadingDialog loadingDialog;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        mContext = this;
        setContentView(getLayoutId());
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorApp));
        initView(savedInstanceState);
        initData();
        AppManager.getAppManager().addActivity(this);
        Log.i(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        Log.i(TAG, "onDestroy");
    }


    public abstract int getLayoutId();

    public abstract void initView(Bundle savedInstanceState);

    public abstract void initData();

    public void startActivity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
    }

    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    public void showLoadingDialog(String title) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        if (loadingDialog.isShowing()) {
            return;
        }
        loadingDialog.setTitle(title);
        loadingDialog.show();

    }

    public void dismissLoadingDialog() {
        LoadingDialog.dismissDialog(loadingDialog);
    }

}
