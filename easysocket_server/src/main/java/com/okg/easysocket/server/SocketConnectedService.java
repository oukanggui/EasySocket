package com.okg.easysocket.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.okg.easysocket.server.executor.ThreadExecutor;
import com.okg.easysocket.server.manager.EasyServerSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static boolean isExit = false;

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
        isExit = true;
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
                while (!isExit) {
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
            Log.d(TAG, "SocketHandleRunnable---->run，处理客户端Socket链接请求");
            if (mSocket != null && !mSocket.isClosed() && !mSocket.isInputShutdown()) {
                try {
                    InputStream is = mSocket.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
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
                    Log.d(TAG, "SocketHandleRunnable---->run，收到客户端请求内容：\n" + message);
                    sendMessageBack(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "SocketHandleRunnable---->run exception occur ,exception = " + e);
                }
            } else {
                Log.d(TAG, "SocketHandleRunnable---->run，Socket状态异常");
            }
        }

        private boolean sendMessageBack(String message) {
            Log.d(TAG, "SocketHandleRunnable---->sendMessageBack，服务端准备回复消息：\n" + message);
            try {
                if (mSocket != null && !mSocket.isClosed() && !mSocket.isOutputShutdown()) {
                    OutputStream os = mSocket.getOutputStream();
                    os.write(message.getBytes());
                    os.flush();
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
