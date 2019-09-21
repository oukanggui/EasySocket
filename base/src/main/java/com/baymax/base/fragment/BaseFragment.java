package com.baymax.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;

import com.baymax.base.widget.StateView;

/**
 * @author oukanggui
 * fragment 基类
 * 使用StateView注入加载动画、加载失败、加载无数据
 */

public abstract class BaseFragment extends LazyLoadFragment {
    public StateView mStateView;
    protected String TAG = getClass().getSimpleName();
    protected Activity mActivity;
    //缓存Fragment view
    private View rootView;

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        super.onAttach(context);
        Log.i(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            onViewInflated(rootView);
            if (mStateView == null && getStateViewRoot() != null) {
                mStateView = StateView.inject(getStateViewRoot());
            }
            initUI(rootView, savedInstanceState);
            initData();
        } else {
            if (mStateView == null) {
                mStateView = StateView.inject(getStateViewRoot());
            }
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    public abstract int getLayoutId();

    /**
     * 初始化控件
     */
    public abstract void initUI(View view, @Nullable Bundle savedInstanceState);

    /**
     * 在监听器之前把数据准备好
     */
    public void initData() {

    }


    //加载数据
    protected abstract void loadData();

    @Override
    protected void onFragmentFirstVisible() {
        //当第一次可见的时候，加载数据
        loadData();
        Log.i(TAG, "onFragmentFirstVisible loadData");
    }

    /**
     * StateView的根布局，默认是整个界面，如果需要变换可以重写此方法
     */
    public View getStateViewRoot() {
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "setUserVisibleHint = " + isVisibleToUser);
    }

    /**
     * 在onViewCreated中执行，当Root View inflate完成时进行调用
     * 目前提供该接口给上层应用进行ButterKnife进行绑定操作
     *
     * @param view
     */
    protected void onViewInflated(View view) {

    }
}
