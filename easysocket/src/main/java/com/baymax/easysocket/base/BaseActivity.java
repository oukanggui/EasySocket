package com.baymax.easysocket.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.listener.OnSocketResponseListener;
import com.baymax.easysocket.manager.EasySocket;

/**
 * @author oukanggui
 * @date 2019/9/3
 * 描述：Activity基类，主要处理Socket响应监听以及一些接口初始化
 */
public abstract class BaseActivity extends AppCompatActivity {

    private OnSocketResponseListener mOnSocketResponseListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView(savedInstanceState);
        initData();
        if (isNeedRegisterSocketResponseListener()) {
            registerSocketResponseListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isNeedRegisterSocketResponseListener()) {
            unregisterSocketResponseListener();
        }
    }

    /**
     * 是否需要注册Socket响应监听器，默认为false监听
     * 需要监听的页面可以重写该方法返回true即可
     *
     * @return
     */
    protected boolean isNeedRegisterSocketResponseListener() {
        return false;
    }

    /**
     * 注册Socket响应监听器
     */
    private void registerSocketResponseListener() {
        if (mOnSocketResponseListener == null) {
            mOnSocketResponseListener = new OnSocketResponseListener() {
                @Override
                public void onResponse(SocketBean socketBean) {
                    // 转由上层接口处理
                    onHandleSocketResponse(socketBean);
                }
            };
        }
        EasySocket.getInstance(this).registerSocketResponseListener(mOnSocketResponseListener);
    }

    /**
     * 注销Socket响应监听器
     */
    private void unregisterSocketResponseListener() {
        EasySocket.getInstance(this).unregisterSocketResponseListener(mOnSocketResponseListener);
    }

    /**
     * 处理客户端响应，如果需要处理Socket响应数据，子类直接继承该方法处理即可
     *
     * @param socketBean 响应的数据实体
     */
    protected void onHandleSocketResponse(SocketBean socketBean) {

    }

    /**
     * 获取布局文件LayoutId
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化View
     *
     * @param savedInstanceState
     */
    public abstract void initView(Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    public abstract void initData();

}
