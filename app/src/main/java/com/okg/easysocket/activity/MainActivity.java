package com.okg.easysocket.activity;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baymax.easysocket.base.BaseActivity;
import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.listener.OnSocketRequestListener;
import com.baymax.easysocket.manager.EasySocket;
import com.baymax.easysocket.util.JsonUtil;
import com.okg.easysocket.R;


/**
 * @author oukanggui
 * 演示Activity
 */
public class MainActivity extends BaseActivity {
    private Button btnSend;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        btnSend = findViewById(R.id.main_btn_send);
    }

    @Override
    public void initData() {
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //初始化EasySocket管理器
                EasySocket.getInstance(MainActivity.this).init("localhost", 8082);
            }
        }, 2000);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketBean socketBean = new SocketBean();
                socketBean.type = 1;
                socketBean.deviceId = "001";
                socketBean.data = "我是App客户端";
                EasySocket.getInstance(MainActivity.this).enqueueRequest(socketBean, new OnSocketRequestListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String reason) {
                        Toast.makeText(MainActivity.this, "发送成功失败，reason = " + reason, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected boolean isNeedRegisterSocketResponseListener() {
        return true;
    }

    @Override
    protected void onHandleSocketResponse(SocketBean socketBean) {
        Toast.makeText(this, "客户端收到服务器消息：" + JsonUtil.convertSocketBeanToJson(socketBean), Toast.LENGTH_SHORT).show();
    }
}

