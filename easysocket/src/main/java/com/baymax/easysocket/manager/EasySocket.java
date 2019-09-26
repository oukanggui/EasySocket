package com.baymax.easysocket.manager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baymax.easysocket.bean.SocketBean;
import com.baymax.easysocket.handler.SocketInitHandler;
import com.baymax.easysocket.handler.SocketRequestHandler;
import com.baymax.easysocket.handler.SocketResponseHandler;
import com.baymax.easysocket.listener.OnSocketInitListener;
import com.baymax.easysocket.listener.OnSocketReadListener;
import com.baymax.easysocket.listener.OnSocketRequestListener;
import com.baymax.easysocket.listener.OnSocketResponseListener;
import com.baymax.utilslib.JsonUtil;
import com.baymax.utilslib.NetworkUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author oukanggui
 * @date 2019/8/26
 * 描述：Socket连接管理器
 * 1、Socket连接初始化（线程处理）
 * 2、关闭socket连接
 * 3、发送消息：写数据（线程管理，如何切换到线程回调）
 * 4、接收消息：读数据（接收线程监听，如何发送到通知界面）
 * 5、心跳管理：（本质上是发送数据）
 * ! 与网络有关均需要在线程中处理，eg socket连接、发送socket请求、读取socket流数据
 * <p>
 * 需要考虑的问题
 * 1）如何以及何时出错重连
 * 2）如何返回服务器发送的数据
 */
public class EasySocket implements OnSocketReadListener {
    private static final String TAG = EasySocket.class.getSimpleName();
    private static EasySocket sEasySocket;
    private static Context mContext;
    /**
     * 与服务器连接的Socket示例
     */
    private static Socket mSocket;

    /**
     * 服务器主机地址，支持配置
     */
    private String mServerHost;

    /**
     * 服务器端口号，支持配置
     */
    private int mServerPort;

    /**
     * Socket初始化连接超时时间
     */
    private static final int TIMEOUT_CONNECT_INIT = 10 * 1000;

    /**
     * 心跳包时间间隔，支持配置，默认60秒
     */
    private static final int HEART_BEAT_RATE_INTERVAL_MILLIS = 60 * 1000;

    /**
     * 心跳包超时间隔，支持配置，默认80秒
     */
    private static final int HEART_BEAT_TIMEOUT_INTERNAL_MILLIS = 80 * 1000;

    /**
     * 记录上次成功发送的时间，减少心跳包发送的频次
     */
    private static long mLastRequestTime;

    /**
     * 心跳线程Runnable
     */
    private HeartBeatRunnable mHeartBeatRunnable;

    /**
     * 心跳超时处理线程Runnable
     */
    private HeartBeatTimeoutRunnable mHeartBeatTimeoutRunnable;

    /**
     * Socket请求处理器
     */
    private SocketRequestHandler mSocketRequestHandler;

    /**
     * Socket读数据线程处理器
     */
    private SocketResponseHandler mSocketResponseHandler;

    /**
     * UI Handler
     */
    private Handler mUIHandler;

    BroadcastReceiver mNetworkChangeReceiver;

    /**
     * Socket响应回调监听器集合
     */
    private List<OnSocketResponseListener> mOnSocketResponseListeners = Collections.synchronizedList(new ArrayList<OnSocketResponseListener>());

    /**
     * Socket初始化监听器
     */
    private OnSocketInitListener mOnSocketInitListener;


    private EasySocket(Context context) {
        mContext = context.getApplicationContext();
        mUIHandler = new Handler(mContext.getMainLooper());
        mHeartBeatRunnable = new HeartBeatRunnable();
        mHeartBeatTimeoutRunnable = new HeartBeatTimeoutRunnable();
        mSocketRequestHandler = new SocketRequestHandler(mContext);
    }

    public static EasySocket getInstance(Context context) {
        if (sEasySocket == null) {
            synchronized (EasySocket.class) {
                if (sEasySocket == null) {
                    sEasySocket = new EasySocket(context);
                }
            }
        }
        return sEasySocket;
    }

    /**
     * 初始化Socket连接,对外提供的接口
     * ！在使用前必须调用此方式进行初始化
     *
     * @param serverHost 要连接的服务器主机Host
     * @param serverPort 要连接的服务器端口
     */
    public void init(String serverHost, int serverPort) {
        init(serverHost, serverPort, null);
    }

    /**
     * 初始化Socket连接,对外提供的接口
     * ！在使用前必须调用此方式进行初始化
     *
     * @param serverHost           要连接的服务器主机Host
     * @param serverPort           要连接的服务器端口
     * @param onSocketInitListener 初始化监听器
     */
    public void init(String serverHost, int serverPort, OnSocketInitListener onSocketInitListener) {
        if (TextUtils.isEmpty(serverHost)) {
            throw new IllegalStateException("serverHost cannot be empty!!");
        }
        mServerHost = serverHost;
        mServerPort = serverPort;
        if (onSocketInitListener != null) {
            mOnSocketInitListener = onSocketInitListener;
        }
        new SocketInitHandler(mContext).start();
    }

    /**
     * 初始化连接,在SocketInitHandler线程执行
     * step 1:创建客户端Socket并设置进行keepAlive的TCP长连接
     * step 2:启动读线程，用于监听服务器发送的数据
     * step 3:心跳和超时管理-初始化成功后，就准备发送心跳包和超时监听
     */
    public void initSocketConnection() {
        // 运行于SocketInitHandler线程中
        try {
            // 建立连接
            mSocket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(mServerHost, mServerPort);
            mSocket.connect(socketAddress, TIMEOUT_CONNECT_INIT);
            mSocket.setKeepAlive(true);
            // 启动SocketResponseHandler线程，监听服务器响应数据
            mSocketResponseHandler = new SocketResponseHandler(mContext, EasySocket.this);
            mSocketResponseHandler.start();
            Log.d(TAG, "init success---->turn to link confirmation......");
            // 链路确认
            linkConfirmation();
            // 启动心跳流程
            postHeartBeatRunnableDelay();
        } catch (UnknownHostException e) {
            // TODO 初始化失败如何重连
            e.printStackTrace();
            Log.e(TAG, "init exception----> e = " + e);
            registerNetworkChangeBroadcast();
            callbackSocketInitResult(false, "" + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "init exception----> e = " + e);
            registerNetworkChangeBroadcast();
            callbackSocketInitResult(false, "" + e);
        }
    }

    /**
     * 重新连接,需要先释放Socket资源
     * step 1：释放资源
     * step 2：重新连接（重新走初始化流程）
     */
    public void reconnectSocket() {
        Log.d(TAG, "Socket connection is ready to release and reconnectSocket");
        releaseSocketConnection();
        init(mServerHost, mServerPort);
    }

    /**
     * Socket相关资源释放入口
     * 1) 移除延时心跳和超时Runnable
     * 2) 关闭释放原来的socket连接
     */
    private void releaseSocketConnection() {
        Log.d(TAG, "socket resource is ready to be released............");
        removeHeartBeatRunnable();
        releaseSocket();
    }

    /**
     * 释放已连接的socket资源
     */
    private void releaseSocket() {
        try {
            if (mSocket != null) {
                if (!mSocket.isClosed()) {
                    mSocket.close();
                }
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "socket resource release exception = " + e);
        }
    }

    /**
     * 获取连接的Socket，须保证Socket已初始化，否则会抛出异常
     *
     * @return
     */
    public Socket getConnectedSocket() {
        isSocketValid();
        return mSocket;
    }

    /**
     * 判断socket是否可用：没有初始化成功或者已关闭，则socket不可用
     *
     * @return
     */
    private boolean isSocketValid() {
        if (mSocket == null) {
            // throw new IllegalStateException("please call initSocketConnect method to init before using");
            Log.e(TAG, "please call init() method to init before using");
            return false;
        }
        return !mSocket.isClosed();
    }

    /**
     * 更新记录最后一次发送时间的间隔，节省心跳间隔时间
     */
    private void updateLastRequestTime() {
        mLastRequestTime = System.currentTimeMillis();
    }

    /**
     * 发送链路确认，进行身份认证
     * 处理问题：如何保证服务器是否已启动，需要发送身份确认消息或心跳数据，保证链路是通的，存在服务端已关闭，但connect是正常的
     *
     * @return 失败原因：连接成功返回null
     */
    private void linkConfirmation() {
        SocketBean socketBean = new SocketBean();
        socketBean.type = 0;
        socketBean.deviceId = "001";
        Log.d(TAG, " send link confirmation heart beat data,message: \n" + JsonUtil.toJson(socketBean));
        enqueueRequest(socketBean, new OnSocketRequestListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "linkConfirmation -->socket is connected");
                // 需要第二次连接确认
                callbackSocketInitResult(true, "socket is connected......");
            }

            @Override
            public void onFailure(String reason) {
                Log.d(TAG, "linkConfirmation --> socket link init exception = " + reason);
                // 初始化失败，回调给客户端处理，TODO 初始化失败或发送失败，待心跳重连还是延时重连？
                callbackSocketInitResult(false, reason);
            }
        });
    }

    /**
     * 处理Socket初始化结果的回调
     *
     * @param isInitSuccess
     * @param reason
     */
    private void callbackSocketInitResult(final boolean isInitSuccess, final String reason) {
        // 初始化结果切换到主线程回调
        if (mOnSocketInitListener != null) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isInitSuccess) {
                        mOnSocketInitListener.onSocketInitSuccess(reason);
                    } else {
                        mOnSocketInitListener.onSocketInitFailure(reason);
                    }
                }
            });
        }
    }

    /**
     * 发送准备发送心跳包和超时监听Runnable
     */
    private void postHeartBeatRunnableDelay() {
        mUIHandler.postDelayed(mHeartBeatRunnable, HEART_BEAT_RATE_INTERVAL_MILLIS);
        mUIHandler.postDelayed(mHeartBeatTimeoutRunnable, HEART_BEAT_TIMEOUT_INTERNAL_MILLIS);
    }

    /**
     * 移除心跳包和超时监听Runnable
     */
    private void removeHeartBeatRunnable() {
        mUIHandler.removeCallbacks(mHeartBeatRunnable);
        mUIHandler.removeCallbacks(mHeartBeatTimeoutRunnable);
    }

    /**
     * 注册Socket读监听器，当客户端通过Socket读到服务器数据时回调
     *
     * @param onSocketResponseListener
     */
    public void registerSocketResponseListener(OnSocketResponseListener onSocketResponseListener) {
        if (onSocketResponseListener != null && !mOnSocketResponseListeners.contains(onSocketResponseListener)) {
            mOnSocketResponseListeners.add(onSocketResponseListener);
        }
    }

    /**
     * 注销Socket读监听器，当客户端页面不可用时，需要同步注销监听器
     *
     * @param onSocketResponseListener
     */
    public void unregisterSocketResponseListener(OnSocketResponseListener onSocketResponseListener) {
        if (onSocketResponseListener != null) {
            mOnSocketResponseListeners.remove(onSocketResponseListener);
        }
    }

    /**
     * 注册网络状态变化广播,在初始化失败时再注册
     */
    private void registerNetworkChangeBroadcast() {
        if (mNetworkChangeReceiver == null) {
            mNetworkChangeReceiver = new NetworkChangeReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(mNetworkChangeReceiver, intentFilter);
        }
    }

    /**
     * 发送消息
     *
     * @param socketBean 待发送的消息数据实体对象
     * @param callback   回调监听器
     */
    public void enqueueRequest(SocketBean socketBean, final OnSocketRequestListener callback) {
        if (socketBean == null) {
            throw new IllegalStateException("enqueueRequest--->socketBean param cannot be null");
        }
        enqueueRequest(JsonUtil.toJson(socketBean), callback);
    }

    /**
     * 发送消息
     *
     * @param message  待发送的消息
     * @param callback 回调监听器
     */
    public void enqueueRequest(final String message, final OnSocketRequestListener callback) {
        if (TextUtils.isEmpty(message)) {
            throw new IllegalStateException("enqueueRequest--->message cannot be empty");
        }
        mSocketRequestHandler.enqueueRequest(message, new OnSocketRequestListener() {
            @Override
            public void onSuccess() {
                // 更新记录最后一次发送时间的间隔，节省心跳间隔时间
                updateLastRequestTime();
                switchCallbackToUIThread(callback, true, null);
            }

            @Override
            public void onFailure(String reason) {
                switchCallbackToUIThread(callback, false, reason);
            }
        });
    }

    /**
     * 把发送消息回调切换到主线程执行
     *
     * @param callback
     * @param success
     * @param reason
     */
    private void switchCallbackToUIThread(final OnSocketRequestListener callback, final boolean success, final String reason) {
        if (callback == null) {
            Log.d(TAG, "callback is null ,no need to handle callback");
            return;
        }
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    Log.d(TAG, "enqueueRequest success callback, reason = " + reason);
                    callback.onSuccess();
                } else {
                    Log.d(TAG, "enqueueRequest error callback, reason = " + reason);
                    callback.onFailure(reason);
                }
            }
        });
    }


    /**
     * 处理心跳回复，需要取消心跳超时Handler消息
     *
     * @param socketBean
     */
    @Override
    public void onReceiveHeartBeatData(SocketBean socketBean) {
        mUIHandler.removeCallbacks(mHeartBeatTimeoutRunnable);
    }

    /**
     * 收到业务数据回调，切换到主线程回调到监听处
     *
     * @param socketBean
     */
    @Override
    public void onReceiveBusinessData(final SocketBean socketBean) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnSocketResponseListener onSocketResponseListener : mOnSocketResponseListeners) {
                    if (onSocketResponseListener != null) {
                        onSocketResponseListener.onResponse(socketBean);
                    }
                }
            }
        });
    }

    private void sendHeartBeatData() {
        SocketBean socketBean = new SocketBean();
        socketBean.type = 0;
        socketBean.deviceId = "001";
        Log.d(TAG, "HeartBeatRunnable --> run ,send heart beat data,message: \n" + JsonUtil.toJson(socketBean));
        enqueueRequest(socketBean, new OnSocketRequestListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "HeartBeatRunnable --> run ,send heart beat data success，I am alive");
                // 继续发送延时心跳包和超时Runnable
                removeHeartBeatRunnable();
                postHeartBeatRunnableDelay();
            }

            @Override
            public void onFailure(String reason) {
                Log.d(TAG, "HeartBeatRunnable --> run ,send heart beat data fail，errorMessage = " + reason);
                // TODO 优化点:心跳包发送失败后，需要重连，可以根据网络是否可用决定重连的时间
                // TODO 1、网络可用，立即重连，网络不可以用，延时重连，是否需要控制重连的次数，超过一定重连次数后，不再重连
                // 心跳包发送失败后，进行重连
                reconnectSocket();
            }
        });
    }

    /**
     * 心跳超时Runnable
     */
    private class HeartBeatTimeoutRunnable implements Runnable {

        @Override
        public void run() {
            Log.e(TAG, "send heart beat data timeout");
            // TODO 心跳包超时后，是否需要根据网络状态进行重连
            reconnectSocket();
        }
    }

    /**
     * 心跳Runnable
     */
    private class HeartBeatRunnable implements Runnable {

        @Override
        public void run() {
            Log.d(TAG, "HeartBeatRunnable --> run ,ready to send heart beat data");
            if (System.currentTimeMillis() - mLastRequestTime >= HEART_BEAT_RATE_INTERVAL_MILLIS) {
                sendHeartBeatData();
            } else {
                Log.d(TAG, "HeartBeatRunnable --> run ,距离上次发送成功时间间隔低于心跳高时间间隔，无需发送心跳包，心跳包重新计时");
                // 心跳包重新计时
                removeHeartBeatRunnable();
                postHeartBeatRunnableDelay();
            }
        }
    }


    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.isNetworkAvailable(context)) {
                Log.d(TAG, "Current network is available");
                if (!isSocketValid()) {
                    Log.d(TAG, "Current network is available,socket wasn't initialized successfully or was close,just reinitialize");
                    init(mServerHost, mServerPort);
                }
            } else {
                Log.e(TAG, "Current network is unavailable");
            }
        }
    }


}
