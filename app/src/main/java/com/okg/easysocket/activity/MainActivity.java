package com.okg.easysocket.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baymax.utilslib.SoftInputUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.base.BaseAppActivity;
import com.okg.easysocket.fragment.HomeFragment;
import com.okg.easysocket.fragment.MineFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author oukanggui
 * 演示Activity
 */
public class MainActivity extends BaseAppActivity {
    @BindView(R.id.main_root_view)
    LinearLayout mainRootView;

    @BindView(R.id.main_bottom_view)
    LinearLayout mainBottomView;

    @BindView(R.id.main_rl_home)
    View bottomHome;

    @BindView(R.id.main_rl_release)
    View bottomRelease;

    @BindView(R.id.main_rl_mine)
    View bottomMine;

    @BindView(R.id.main_tv_home)
    TextView tv_home;

    @BindView(R.id.main_tv_release)
    TextView tv_release;

    @BindView(R.id.main_tv_mine)
    TextView tv_mine;

    @BindView(R.id.main_iv_home)
    ImageView iv_home;

    @BindView(R.id.main_iv_release)
    ImageView iv_release;

    @BindView(R.id.main_iv_mine)
    ImageView iv_mine;

    boolean isFirst = true;

    private List<Fragment> fragmentList;
    private HomeFragment homeFragment;
    private MineFragment mineFragment;
    private boolean isExit = false;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                isExit = false;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initFragment();
        switchFragment(0);
        //初始选中首页
        changeBarState(R.id.main_rl_home);
    }

    @OnClick({R.id.main_rl_home, R.id.main_rl_release, R.id.main_rl_mine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_rl_home:
                changeBarState(R.id.main_rl_home);
                switchFragment(0);
                break;
            case R.id.main_rl_release:
                startActivity(DeviceEditActivity.class);
                break;
            case R.id.main_rl_mine:
                changeBarState(R.id.main_rl_mine);
                switchFragment(1);
                break;
            default:
                break;
        }

    }


    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        homeFragment = new HomeFragment();
        mineFragment = new MineFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(mineFragment);
        for (Fragment fragment : fragmentList) {
            getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, fragment).commit();
        }
    }

    private void hideFragments() {
        for (Fragment fragment : fragmentList) {
            getSupportFragmentManager().beginTransaction().hide(fragment).commit();
        }
    }

    private void switchFragment(int position) {
        hideFragments();
        getSupportFragmentManager().beginTransaction().show(fragmentList.get(position)).commit();
    }

    private void changeBarState(int viewId) {
        clearBarState();
        int color = ContextCompat.getColor(this, R.color.colorApp);
        if (viewId == R.id.main_rl_home) {
            tv_home.setTextColor(color);
            iv_home.setImageResource(R.mipmap.ic_home_press);
            return;
        }
        if (viewId == R.id.main_rl_release) {
            tv_release.setTextColor(color);
            return;
        }
        if (viewId == R.id.main_rl_mine) {
            tv_mine.setTextColor(color);
            iv_mine.setImageResource(R.mipmap.ic_mine_press);
            return;
        }

        return;
    }

    private void clearBarState() {
        int color = ContextCompat.getColor(this, R.color.color_text);
        tv_home.setTextColor(color);
        tv_release.setTextColor(color);
        tv_mine.setTextColor(color);

        iv_home.setImageResource(R.mipmap.ic_home_normal);
        iv_mine.setImageResource(R.mipmap.ic_mine_normal);

    }

    @Override
    public void onBackPressed() {
        SoftInputUtil.hideSoftInput(this);
        if (isExit) {
            finish();
        } else {
            ToastUtil.showToast(MainActivity.this, "再按一下退出应用");
            handler.sendEmptyMessageDelayed(0x123, 2000);
            isExit = true;
        }
    }
}

