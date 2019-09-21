package com.baymax.base.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.base.R;


/**
 * ************************************************ <br>
 * 创建人员: oukanggui <br>
 * 文件描述: 带headerview的activity基类<br>
 * 修改时间: 2018/1/18 <br>
 * ************************************************
 */
public abstract class BaseToorBarActivity extends BaseActivity {

    private static final String TAG = BaseToorBarActivity.class.getSimpleName();
    // View start

    public FrameLayout mTitleLayout = null;

    private View mHeadLayout = null;

    private View mBackLayout = null;

    private TextView mTitleTView = null;

    private FrameLayout mContainer;
    // View end

    @Override
    public void setContentView(View view, LayoutParams params) {
        if (needReset()) {
            super.setContentView(view, params);
            return;
        }
        setContentView(R.layout.base_ui_layout);
        mContainer = (FrameLayout) findViewById(R.id.container);
        mContainer.addView(view, params);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (needReset()) {
            super.setContentView(layoutResID);
        } else {
            super.setContentView(R.layout.base_ui_layout);
            mContainer = (FrameLayout) findViewById(R.id.container);
            if (layoutResID != R.layout.base_ui_layout) {
                View view = LayoutInflater.from(this).inflate(layoutResID, null);
                mContainer.addView(view);
            }
            initHeadLayout();
        }
    }

    @Override
    public void setContentView(View view) {
        if (needReset()) {
            super.setContentView(view);
            return;
        }
        setContentView(R.layout.base_ui_layout);
        mContainer = (FrameLayout) findViewById(R.id.container);
        mContainer.addView(view);
    }

    protected void initHeadLayout() {
        if (!needShowHeadLayout()) {
            return;
        }
        View view = findViewById(R.id.head_layout_viewstub);
        if (view instanceof ViewStub) {
            ViewStub viewStub = (ViewStub) view;
            mHeadLayout = viewStub.inflate();
        } else {
            throw new IllegalArgumentException("Need show head layout, but no head view.");
        }
        //mTitleLayout = (FrameLayout) mHeadLayout.findViewById(R.id.base_);
        mTitleLayout = (FrameLayout) findViewById(R.id.base_title_layout);
        //StatusBarUtil.setTranslucentForImageView(this, 0, mTitleLayout);
        //StatusBarUtil.setStatusBarDark(this,true);
        // 1、返回
        mBackLayout = mHeadLayout.findViewById(R.id.base_back);
        if (mBackLayout != null) {
            mBackLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    // 返回上一页
                    finish();
                }
            });
        }
        // 2、Title
        mTitleTView = mHeadLayout.findViewById(R.id.base_title);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Head栏的布局
     */
    protected int getHeadLayoutRes() {
        return R.layout.base_header_view;
    }

    /**
     * 是否需要显示Head栏
     */
    protected boolean needShowHeadLayout() {
        return true;
    }

    public View getHeadLayout() {
        return mHeadLayout;
    }

    /**
     * 如果子类不用提供的好的布局方式，则重写此方法 [true]: 不采用父Layout中的Head；[false]：相反
     *
     * @return
     */
    protected boolean needReset() {
        return false;
    }

    /**
     * 是否显示返回按钮
     *
     * @param isShow void
     */
    public void showBackLayout(boolean isShow) {
        if (mBackLayout != null) {
            if (isShow) {
                mBackLayout.setVisibility(View.VISIBLE);
            } else {
                mBackLayout.setVisibility(View.GONE);
            }
        }
    }

    public View getBackLayout() {
        return mBackLayout;
    }

    /**
     * 是否显示标题
     *
     * @param isShow void
     */
    public void showTitle(boolean isShow) {
        if (mTitleTView != null) {
            if (isShow) {
                mTitleTView.setVisibility(View.VISIBLE);
            } else {
                mTitleTView.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 设置标题
     *
     * @param title
     */
    protected void setHeaderTitle(String title) {
        if (mTitleTView != null) {
            mTitleTView.setText(title);
        }
    }

    /**
     * 设置标题
     *
     * @param title
     * @param colorId
     */
    protected void setHeaderTitle(String title, int colorId) {
        if (mTitleTView != null) {
            mTitleTView.setText(title);
            mTitleTView.setTextColor(mContext.getResources().getColor(colorId));
        }
    }

    /**
     * 设置标题
     *
     * @param resId
     */
    protected void setHeaderTitle(int resId) {
        if (mTitleTView != null) {
            mTitleTView.setText(resId);
        }
    }

    /**
     * 设置head背景色
     *
     * @param resId
     */
    public void setHeadBgImageResource(int resId) {
        if (mTitleLayout != null && resId > 0) {
            mTitleLayout.setBackgroundResource(resId);
        }
    }

}
