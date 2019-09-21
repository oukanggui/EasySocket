package com.okg.easysocket.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.listener.OnSocketResponseListener;
import com.baymax.easysocket.manager.EasySocket;

/**
 * @author Baymax
 * @date 2019-09-20
 * 描述：Socket通信基类
 */
public abstract class BaseSocketFragment extends BaseAppFragment {
    private OnSocketResponseListener mOnSocketResponseListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerSocketResponseListener();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterSocketResponseListener();
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
        EasySocket.getInstance(mActivity).registerSocketResponseListener(mOnSocketResponseListener);
    }

    /**
     * 注销Socket响应监听器
     */
    private void unregisterSocketResponseListener() {
        EasySocket.getInstance(mActivity).unregisterSocketResponseListener(mOnSocketResponseListener);
    }

    /**
     * 处理客户端响应，如果需要处理Socket响应数据，子类直接继承该方法处理即可
     *
     * @param socketBean 响应的数据实体
     */
    protected abstract void onHandleSocketResponse(SocketBean socketBean);
}
