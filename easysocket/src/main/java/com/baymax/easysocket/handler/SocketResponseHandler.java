package com.baymax.easysocket.handler;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.listener.OnSocketReadListener;
import com.baymax.easysocket.manager.EasySocket;
import com.baymax.easysocket.util.StringUtil;
import com.baymax.utilslib.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author oukanggui
 * @date 2019/8/28
 * 描述：Socket读处理器Thread
 */
public class SocketResponseHandler extends Thread {
    private static final String TAG = SocketResponseHandler.class.getSimpleName();
    private Context mContext;
    private OnSocketReadListener mOnSocketReadListener;

    public SocketResponseHandler(Context context, OnSocketReadListener onSocketReadListener) {
        mContext = context;
        mOnSocketReadListener = onSocketReadListener;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "SocketResponseHandler run............");
        Socket socket = EasySocket.getInstance(mContext).getConnectedSocket();
        if (socket != null) {
            try {
                while (!socket.isClosed() && !socket.isInputShutdown()) {
                    InputStream is = socket.getInputStream();
                    BufferedReader input = new BufferedReader(new InputStreamReader(is));
                    String message = "";
                    String line = null;
                    // 读取到"/n/r"结束，此时line值为空，防止一直阻塞在readLine方法
                    while ((line = input.readLine()) != null && !TextUtils.isEmpty(line)) {
                        message += line;
                    }
                    if (!StringUtil.isEmpty(message)) {
                        SocketBean socketBean = JsonUtil.parseJson(message.trim(), SocketBean.class);
                        // 收到服务器过来的消息，有两种方案：
                        // 1、通过本地Broadcast发送出去
                        // 2、通过监听器
                        if (socketBean.isHeartBeatData()) {
                            Log.d(TAG, "Receive heart beat data ,message:\n" + message);
                            if (mOnSocketReadListener != null) {
                                mOnSocketReadListener.onReceiveHeartBeatData(socketBean);
                            }
                        } else {
                            Log.d(TAG, "Receive business data ,message:\n" + message);
                            if (mOnSocketReadListener != null) {
                                mOnSocketReadListener.onReceiveBusinessData(socketBean);
                            }
                        }
                    } else {
                        Log.d(TAG, "Receive message from server is empty，server is offline，just close the socket connection and reConnect");
                        socket.close();
                        // TODO 关闭后，何时重连
                        //EasySocket.getInstance(mContext).reconnectSocket();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "SocketResponseHandler run exception occur ,exception = " + e);
            }
        }
    }
}
