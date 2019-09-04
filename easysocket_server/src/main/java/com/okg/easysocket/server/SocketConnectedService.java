package com.okg.easysocket.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.okg.easysocket.server.executor.ThreadExecutor;
import com.okg.easysocket.server.manager.EasyServerSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author oukanggui
 * @date 2019/9/3
 * 描述：Socket连接服务，用于演示处理App客户端请求
 */
public class SocketConnectedService extends Service {
    private static final String TAG = SocketConnectedService.class.getSimpleName();
    /**
     * 服务端ServerSocket对象，用于监听客户端连接请求
     */
    private static ServerSocket mServerSocket;

    /**
     * 判断服务是否已退出
     */
    private static boolean mIsServiceDestroyed = false;

    /**
     * 线程池执行器
     */
    private ThreadExecutor mThreadExecutor;

    /**
     * ServerSocket运行和监听Runnable
     */
    private ServerRunnable mServerRunnable;

    private NotificationManager mNotificationManager;
    private static final String NOTIFICATION_CHANNEL_ID = "ServerSocket";
    private static final String NOTIFICATION_CHANNEL_NAME = "Server-Socket";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            startForeground(1, getNotification());
        }
        mThreadExecutor = ThreadExecutor.getInstance();
        mServerRunnable = new ServerRunnable();
        mThreadExecutor.queueTask(mServerRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestroyed = true;
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ServerSocket关闭异常.........");
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("ServerSocket服务")
                .setContentText("我正在运行");
        return builder.build();
    }

    private class ServerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(EasyServerSocket.getInstance().getSocketListenPort());
                Log.d(TAG, "ServerSocket已启动.........");
                while (!mIsServiceDestroyed) {
                    Log.d(TAG, "ServerSocket正在监听请求.........");
                    Socket socket = mServerSocket.accept();
                    Log.d(TAG, "ServerSocket收到请求.........，客户端地址：" + socket.getRemoteSocketAddress().toString());
                    mThreadExecutor.queueTask(new SocketHandleRunnable(socket));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ServerSocket启动异常.........,e = " + e);
            }

        }
    }


    private class SocketHandleRunnable implements Runnable {
        private Socket mSocket;

        public SocketHandleRunnable(Socket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            Log.d(TAG, "SocketHandleRunnable---->run，处理客户端Socket连接请求");
            while (mSocket != null && !mSocket.isClosed() && !mSocket.isInputShutdown()) {
                try {
                    InputStream is = mSocket.getInputStream();
                    BufferedReader input = new BufferedReader(new InputStreamReader(is));
                    String message = "";
                    String line = null;
                    // 读取到"/n/r"结束，此时line值为空，防止一直阻塞在readLine方法
                    while ((line = input.readLine()) != null && !TextUtils.isEmpty(line)) {
                        message += line;
                    }
                    Log.d(TAG, "SocketHandleRunnable---->run，收到客户端请求内容：\n" + message);
                    sendMessageBack(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "SocketHandleRunnable---->run exception occur ,exception = " + e);
                    // TODO 是否需要退出重新监听
                }
            }
            Log.d(TAG, "SocketHandleRunnable---->run，Socket状态异常");
        }

        private boolean sendMessageBack(String message) {
            Log.d(TAG, "SocketHandleRunnable---->sendMessageBack，服务端准备回复消息：\n" + message);
            try {
                if (mSocket != null && !mSocket.isClosed() && !mSocket.isOutputShutdown()) {
                    OutputStream os = mSocket.getOutputStream();
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(os));
                    // 由于长连接 os流没有Shutdown，导致服务器一直阻塞在readLine/read方法，即使此时没有数据传输了
                    // readLine()只有在数据流发生异常或者另一端被close()掉时，才会返回null值。
                    // 如果不指定buffer大小，则readLine()使用的buffer有8192个字符。在达到buffer大小之前，只有遇到"/r"、"/n"、"/r/n"才会返回。
                    // 解决方法：在客户端发送数据时，手动给输入内容加入"\n"或"\r"，再写入服务器，服务端写数据也是如此
                    output.write(message);
                    output.write("\n\r");
                    output.flush();
                    return true;
                } else {
                    Log.e(TAG, "SocketHandleRunnable---->sendMessageBack Socket状态异常");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "SocketHandleRunnable---->sendMessageBack exception occur ,exception = " + e);
            }
            return false;
        }
    }


}
