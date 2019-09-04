package com.baymax.easysocket.handler;

import android.content.Context;
import android.util.Log;

import com.baymax.easysocket.listener.OnSocketRequestListener;
import com.baymax.easysocket.manager.EasySocket;
import com.baymax.easysocket.executor.ThreadExecutor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
                        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(os));
                        // 由于长连接 os流没有Shutdown，导致服务器一直阻塞在readLine/read方法，即使此时没有数据传输了
                        // readLine()只有在数据流发生异常或者另一端被close()掉时，才会返回null值。
                        // 如果不指定buffer大小，则readLine()使用的buffer有8192个字符。在达到buffer大小之前，只有遇到"/r"、"/n"、"/r/n"才会返回。
                        // 解决方法：在客户端发送数据时，手动给输入内容加入"\n"或"\r"，再写入服务器，服务端写数据也是如此
                        output.write(message);
                        output.write("\n\r");
                        output.flush();
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
