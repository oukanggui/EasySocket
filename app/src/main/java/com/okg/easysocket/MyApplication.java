package com.okg.easysocket;

import android.app.Application;

import com.baymax.easysocket.manager.EasySocket;
import com.baymax.utilslib.LogUtil;
import com.okg.easysocket.server.manager.EasyServerSocket;


/**
 * @author oukanggui
 * @date 2019/8/27
 * 描述：
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //先启动服务器,再初始化客户端
        //EasyServerSocket.getInstance().startServer(this, 8082);
        init();
    }

    private void init() {
        LogUtil.init(this, true, true);
    }
}
