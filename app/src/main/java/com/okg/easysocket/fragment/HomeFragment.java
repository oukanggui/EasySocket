package com.okg.easysocket.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.listener.OnSocketInitListener;
import com.baymax.easysocket.listener.OnSocketRequestListener;
import com.baymax.easysocket.manager.EasySocket;
import com.baymax.utilslib.ViewUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.adapter.ChatAdapter;
import com.okg.easysocket.base.BaseSocketFragment;
import com.okg.easysocket.bean.ChatBean;
import com.okg.easysocket.constant.AppConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author oukanggui on 2018/2/2.
 */

public class HomeFragment extends BaseSocketFragment {

    @BindView(R.id.home_btn_send)
    Button btnSend;
    @BindView(R.id.home_title)
    TextView tvTitle;
    @BindView(R.id.home_rv_chat_list)
    RecyclerView rvChatRecyclerView;
    private int count = 0;
    private List<ChatBean> mChatList = new ArrayList<>();
    private ChatAdapter mChatAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }


    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("设备" + AppConstants.DEVICE_ID + "---连接中");
        //LinearLayoutLayout即线性布局，创建对象后把它设置到RecyclerView当中
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvChatRecyclerView.setLayoutManager(layoutManager);
        //创建MsgAdapter的实例并将数据传入到MsgAdapter的构造函数中
        mChatAdapter = new ChatAdapter(mChatList);
        rvChatRecyclerView.setAdapter(mChatAdapter);
        //初始化EasySocket管理器
        EasySocket.getInstance(mActivity).init(AppConstants.SERVER_HOST, AppConstants.SERVER_PORT, new OnSocketInitListener() {
            @Override
            public void onSocketInitSuccess(String message) {
                tvTitle.setText("设备" + AppConstants.DEVICE_ID + "---已连接上");
                btnSend.setEnabled(true);
                Toast.makeText(mActivity, "服务器已连接上", Toast.LENGTH_SHORT).show();
                ChatBean chatBean = new ChatBean("服务器已连接上", ChatBean.TYPE_RECEIVED);
                mChatList.add(chatBean);
                //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                mChatAdapter.notifyItemInserted(mChatList.size() - 1);
                //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
                rvChatRecyclerView.scrollToPosition(mChatList.size() - 1);
            }

            @Override
            public void onSocketInitFailure(String reason) {
                tvTitle.setText("设备" + AppConstants.DEVICE_ID + "---连接失败");
                btnSend.setEnabled(false);
            }
        });
    }

    //在Fragment第一次可见是回调
    @Override
    protected void loadData() {
    }

    @Override
    public View getStateViewRoot() {
        return null;
    }


    @Override
    protected void onHandleSocketResponse(SocketBean socketBean) {
        ChatBean chatBean = new ChatBean(socketBean.from == SocketBean.FROM_APP ? socketBean.msg : socketBean.data, ChatBean.TYPE_RECEIVED);
        mChatList.add(chatBean);
        //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
        mChatAdapter.notifyItemInserted(mChatList.size() - 1);
        //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
        rvChatRecyclerView.scrollToPosition(mChatList.size() - 1);
    }

    @OnClick({R.id.home_btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_btn_send:
                if (ViewUtil.isFastDoubleClick()) {
                    return;
                }
                final SocketBean socketBean = new SocketBean();
                socketBean.type = 1;
                socketBean.deviceId = AppConstants.DEVICE_ID;
                socketBean.data = "" + count++;
                EasySocket.getInstance(mActivity).enqueueRequest(socketBean, new OnSocketRequestListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mActivity, "发送成功", Toast.LENGTH_SHORT).show();
                        ChatBean chatBean = new ChatBean(socketBean.data, ChatBean.TYPE_SENT);
                        mChatList.add(chatBean);
                        //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                        mChatAdapter.notifyItemInserted(mChatList.size() - 1);
                        //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
                        rvChatRecyclerView.scrollToPosition(mChatList.size() - 1);
                    }

                    @Override
                    public void onFailure(String reason) {
                        Toast.makeText(mActivity, "发送成功失败，reason = " + reason, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            default:
                break;
        }
    }
}
