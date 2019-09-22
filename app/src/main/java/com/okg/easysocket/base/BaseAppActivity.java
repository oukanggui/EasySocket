package com.okg.easysocket.base;

import android.view.View;
import android.view.ViewGroup;

import com.baymax.base.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Baymax
 * @date 2019-09-21
 * 描述：持ButterKnife注入的Activity基类，需要使用BufferKnife的Activity需要继承该类
 */
public abstract class BaseAppActivity extends BaseActivity {
    Unbinder unbinder;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // BufferKnife需要在setContentView后执行
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
