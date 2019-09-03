package com.okg.easysocket.server.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author oukanggui
 * @date 2019/8/27
 * 描述：线程池管理器，用于处理线程执行
 */
public class ThreadExecutor {
    private static final String TAG = ThreadExecutor.class.getSimpleName();
    public static final int THREAD_POOL_QUEUE_SIZE = 20;
    private final static Object LOCK_QUEUE = new Object();
    private static ExecutorService mExecutorServiceQueue;
    private static ThreadExecutor sThreadExecutor;

    private ThreadExecutor() {
        mExecutorServiceQueue = new java.util.concurrent.ThreadPoolExecutor(THREAD_POOL_QUEUE_SIZE / 10 + 1,
                THREAD_POOL_QUEUE_SIZE, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new SocketThreadFactory("socket-thread"), new java.util.concurrent.ThreadPoolExecutor.DiscardPolicy());
    }

    public static ThreadExecutor getInstance() {
        if (sThreadExecutor == null) {
            synchronized (ThreadExecutor.class) {
                if (sThreadExecutor == null) {
                    sThreadExecutor = new ThreadExecutor();
                }
            }
        }
        return sThreadExecutor;
    }

    /**
     * 在线程池运行线程
     *
     * @param task
     */
    public void queueTask(Runnable task) {
        mExecutorServiceQueue.execute(task);
    }


    private static class SocketThreadFactory implements ThreadFactory {
        private String mThreadName;

        public SocketThreadFactory(String threadName) {
            mThreadName = threadName;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, mThreadName);
        }
    }
}
