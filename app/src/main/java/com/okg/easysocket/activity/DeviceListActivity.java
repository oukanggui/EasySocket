package com.okg.easysocket.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baymax.base.adapter.base.BaseQuickAdapter;
import com.baymax.base.dialog.BaseDialog;
import com.baymax.base.widget.StateView;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.adapter.DeviceListAdapter;
import com.okg.easysocket.api.device.DeviceInterface;
import com.okg.easysocket.base.BaseAppTitleBarActivity;
import com.okg.easysocket.bean.DeviceInfo;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.dialog.MessageDialog;
import com.okg.easysocket.helper.RetrofitLoader;
import com.okg.easysocket.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Retrofit;

public class DeviceListActivity extends BaseAppTitleBarActivity {

    @BindView(R.id.root_view)
    View mRootView;

    @BindView(R.id.device_rv_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private DeviceListAdapter mAdapter;

    private List<DeviceInfo> mList;

    private boolean isFirstOnCreate = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_device_list;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setHeaderTitle("我的设备");
        setRightIcon(R.mipmap.ic_add);
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                getData();
            }
        });


        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorApp);
        mList = new ArrayList();
        mAdapter = new DeviceListAdapter(R.layout.item_list_device, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtil.showToast(mContext, "功能设计中");
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_device_edit:
                        Intent intent = new Intent(mContext, DeviceEditActivity.class);
                        intent.putExtra(DeviceEditActivity.KEY_DEVICE_INFO, mList.get(position));
                        mContext.startActivity(intent);
                        break;
                    case R.id.item_device_delete:
                        handleDeviceDelete(mList.get(position));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(false);
            }
        });
    }


    private void getData() {
        getData(true);
    }

    private void getData(boolean isShowLoading) {
        RetrofitLoader.getInstance().request(new RetrofitLoader.OnRequestListener<List<DeviceInfo>>() {
            @Override
            public Call<ResponseMsg<List<DeviceInfo>>> onCreateCall(Retrofit retrofit) {
                String userName = UserInfoManager.getInstance(mContext).getUserAccount();
                return retrofit.create(DeviceInterface.class).getDeviceList(userName);
            }

            @Override
            public void onBefore() {
                if (isShowLoading) {
                    mStateView.showLoading();
                }
            }

            @Override
            public void onSuccess(String msg, List<DeviceInfo> data) {
                mSwipeRefreshLayout.setRefreshing(false);
                mList = data;
                if (mList == null || mList.size() <= 0) {
                    mStateView.showEmpty();
                } else {
                    mAdapter.setNewData(mList);
                    mStateView.showContent();
                }
            }

            @Override
            public void onFail(String errMsg, int errCode, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mStateView.showRetry();
            }
        });
    }

    private void handleDeviceDelete(DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            return;
        }
        new MessageDialog.Builder((FragmentActivity) mContext)
                // 标题可以不用填写
                .setTitle("删除")
                // 内容必须要填写
                .setMessage("你确定要删除该设备吗？")
                // 确定按钮文本
                .setConfirm("确认")
                // 设置 null 表示不显示取消按钮
                .setCancel("取消")
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener(new MessageDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        RetrofitLoader.getInstance().request(new RetrofitLoader.OnRequestListener<String>() {
                            @Override
                            public Call<ResponseMsg<String>> onCreateCall(Retrofit retrofit) {
                                return retrofit.create(DeviceInterface.class).deleteDevice(deviceInfo.getDeviceId());
                            }

                            @Override
                            public void onBefore() {
                                showLoadingDialog("删除中");
                            }

                            @Override
                            public void onSuccess(String msg, String data) {
                                dismissLoadingDialog();
                                ToastUtil.showToast(mContext, msg);
                                getData(false);
                            }

                            @Override
                            public void onFail(String errMsg, int errCode, Throwable t) {
                                dismissLoadingDialog();
                                ToastUtil.showToast(mContext, "删除失败");
                            }
                        });
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                })
                .show();
    }

    @Override
    public View getStateViewRoot() {
        return mRootView;
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData(isFirstOnCreate);
        isFirstOnCreate = false;
    }

    @Override
    protected void onRightClick() {
        startActivity(DeviceEditActivity.class);
    }
}
