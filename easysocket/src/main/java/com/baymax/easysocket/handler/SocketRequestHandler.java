package com.baymax.easysocket.handler;

import android.content.Context;
import android.util.Log;

import com.baymax.easysocket.listener.OnSocketRequestListener;
import com.baymax.easysocket.manager.EasySocket;
import com.baymax.easysocket.executor.ThreadExecutor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author oukanggui
 * @date 2019/8/28
 * 描述：Socket请求处理器Thread
 */
public class SocketRequestHandler {
    private static final String TAG = SocketRequestHandler.class.getSimpleName();
    private Context mContext;
    /**
     * 线程执行管理器，所有Socket请求均在线程池执行
     */
    private ThreadExecutor mThreadExecutor;


    public SocketRequestHandler(Context context) {
        mContext = context;
        mThreadExecutor = ThreadExecutor.getInstance();
    }

    public void enqueueRequest(final String message, final OnSocketRequestListener callback) {
        Log.d(TAG, "enqueueRequest--->ready to send message to Server");
        Log.d(TAG, "enqueueRequest--->message:\n" + message);
        mThreadExecutor.queueTask(new Runnable() {
            @Override
            public void run() {
                Socket socket = EasySocket.getInstance(mContext).getConnectedSocket();
                try {
                    if (socket != null && !socket.isClosed() && !socket.isOutputShutdown()) {
                        OutputStream os = socket.getOutputStream();
                        os.write(message.getBytes());
                        os.flush();
                        callback.onSuccess();
                    } else {
                        // TODO 如何重连，重连的时间
                        callback.onFailure("Socket connection had been closed ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }


}
