package com.baymax.easysocket.handler;

import android.content.Context;
import android.util.Log;

import com.baymax.easysocket.manager.EasySocket;

/**
 * @author oukanggui
 * @date 2019/8/28
 * 描述：Socket连接初始化处理器Thread
 */
public class SocketInitHandler extends Thread {
    private static final String TAG = SocketInitHandler.class.getSimpleName();
    private Context mContext;

    public SocketInitHandler(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "SocketInitHandler run............");
        EasySocket.getInstance(mContext).initSocketConnection();
    }
}
