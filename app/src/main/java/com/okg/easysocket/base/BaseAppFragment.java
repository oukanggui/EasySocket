package com.okg.easysocket.base;

import android.view.View;

import com.baymax.base.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Baymax
 * @date 2019-09-21
 * 描述：支持ButterKnife注入的Fragment基类，需要使用BufferKnife的Fragment需要继承该类
 */
public abstract class BaseAppFragment extends BaseFragment {
    Unbinder unbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onViewInflated(View view) {
        super.onViewInflated(view);
        unbinder = ButterKnife.bind(this, view);
    }
}
