package com.okg.easysocket.activity;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baymax.easysocket.base.BaseActivity;
import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.listener.OnSocketInitListener;
import com.baymax.easysocket.listener.OnSocketRequestListener;
import com.baymax.easysocket.manager.EasySocket;
import com.okg.easysocket.R;
import com.okg.easysocket.adapter.ChatAdapter;
import com.okg.easysocket.bean.ChatBean;

import java.util.ArrayList;
import java.util.List;


/**
 * @author oukanggui
 * 演示Activity
 */
public class MainActivity extends BaseActivity {
    private static final String DEVICE_ID = "001";
    private Button btnSend;
    private TextView tvTitle;
    private RecyclerView rvChatRecyclerView;
    private int count = 0;
    private List<ChatBean> mChatList = new ArrayList<>();
    private ChatAdapter mChatAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        btnSend = findViewById(R.id.main_btn_send);
        tvTitle = findViewById(R.id.main_tv_title);
        rvChatRecyclerView = findViewById(R.id.main_rv_chat_list);
    }

    @Override
    public void initData() {
        tvTitle.setText("设备" + DEVICE_ID + "---连接中");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SocketBean socketBean = new SocketBean();
                socketBean.type = 1;
                socketBean.deviceId = DEVICE_ID;
                socketBean.data = "" + count++;
                EasySocket.getInstance(MainActivity.this).enqueueRequest(socketBean, new OnSocketRequestListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        ChatBean chatBean = new ChatBean(socketBean.data, ChatBean.TYPE_SENT);
                        mChatList.add(chatBean);
                        //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
                        mChatAdapter.notifyItemInserted(mChatList.size() - 1);
                        //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
                        rvChatRecyclerView.scrollToPosition(mChatList.size() - 1);
                    }

                    @Override
                    public void onFailure(String reason) {
                        Toast.makeText(MainActivity.this, "发送成功失败，reason = " + reason, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        //LinearLayoutLayout即线性布局，创建对象后把它设置到RecyclerView当中
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatRecyclerView.setLayoutManager(layoutManager);
        //创建MsgAdapter的实例并将数据传入到MsgAdapter的构造函数中
        mChatAdapter = new ChatAdapter(mChatList);
        rvChatRecyclerView.setAdapter(mChatAdapter);

        // 启动Socket连接
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //初始化EasySocket管理器
                EasySocket.getInstance(MainActivity.this).init("localhost", 8082, new OnSocketInitListener() {
                    @Override
                    public void onSocketInitSuccess(String message) {
                        tvTitle.setText("设备" + DEVICE_ID + "---已连接上");
                        btnSend.setEnabled(true);
                        Toast.makeText(MainActivity.this, "服务器已连接上，可以与服务器通信聊天了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSocketInitFailure(String reason) {
                        tvTitle.setText("设备" + DEVICE_ID + "---连接失败");
                        btnSend.setEnabled(false);
                    }
                });
            }
        }, 1000);
    }

    @Override
    protected boolean isNeedRegisterSocketResponseListener() {
        return true;
    }

    @Override
    protected void onHandleSocketResponse(SocketBean socketBean) {
        ChatBean chatBean = new ChatBean(socketBean.data, ChatBean.TYPE_RECEIVED);
        mChatList.add(chatBean);
        //调用适配器的notifyItemInserted()用于通知列表有新的数据插入，这样新增的一条消息才能在RecyclerView中显示
        mChatAdapter.notifyItemInserted(mChatList.size() - 1);
        //调用scrollToPosition()方法将显示的数据定位到最后一行，以保证可以看到最后发出的一条消息
        rvChatRecyclerView.scrollToPosition(mChatList.size() - 1);
    }
}

