package com.baymax.easysocket.handler;

import android.content.Context;
import android.util.Log;

import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.listener.OnSocketReadListener;
import com.baymax.easysocket.manager.EasySocket;
import com.baymax.easysocket.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
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
                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[1024 * 4];
                while (!socket.isClosed() && !socket.isInputShutdown()) {
                    int length;
                    StringBuilder stringBuilder = new StringBuilder();
                    // read返回-1，表明流数据已读取完（）
                    // read方法是阻塞的，必须在线程执行，当管道内无数据时read方法会阻塞，除非是如下三中情况会停止阻塞
                    // 1、InputStream数据流有数据时
                    // 2、文件读取完时
                    // 3、发生IO异常时
                    while ((length = is.read(buffer)) != -1) {
                        stringBuilder.append(new String(buffer, 0, length));
                    }
                    String message = stringBuilder.toString();
                    SocketBean socketBean = JsonUtil.parseJsonToSocketBean(message);
                    // 收到服务器过来的消息，有两种方案：
                    // 1、通过本地Broadcast发送出去
                    // 2、通过监听器
                    if (socketBean.isHeartBeatData()) {
                        Log.d(TAG, "Receive heart beat data ,message:\n" + message);
                        if (mOnSocketReadListener != null) {
                            mOnSocketReadListener.onReceiveHeartBeatData(message);
                        }
                    } else {
                        Log.d(TAG, "Receive business data ,message:\n" + message);
                        if (mOnSocketReadListener != null) {
                            mOnSocketReadListener.onReceiveBusinessData(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "SocketResponseHandler run exception occur ,exception = " + e);
            }
        }
    }
}
