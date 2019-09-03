package com.okg.easysocket.server.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.okg.easysocket.server.SocketConnectedService;

/**
 * @author oukanggui
 * @date 2019/9/3
 * 描述：ServerSocket统一管理类
 */
public class EasyServerSocket {
    private static EasyServerSocket sEasyServerSocket;
    private static int sPort;

    private EasyServerSocket() {

    }

    public static EasyServerSocket getInstance() {
        if (sEasyServerSocket == null) {
            synchronized (EasyServerSocket.class) {
                if (sEasyServerSocket == null) {
                    sEasyServerSocket = new EasyServerSocket();
                }
            }
        }
        return sEasyServerSocket;
    }

    /**
     * 启动ServerSocket，监听客户端请求
     *
     * @param context
     * @param port    监听的客户端端口
     */
    public void startServer(Context context, int port) {
        setSocketListenPort(port);
        Intent intent = new Intent(context, SocketConnectedService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Android O服务启动适配
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
    }


    private void setSocketListenPort(int port) {
        if (port < 0) {
            throw new IllegalStateException("ServerSocket listening port cannot be negative number");
        }
        sPort = port;
    }

    /**
     * 获取监听的客户端端口
     *
     * @return
     */
    public int getSocketListenPort() {
        return sPort;
    }
}
