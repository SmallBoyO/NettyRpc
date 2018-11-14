package com.zhanghe.ThreadPool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class RpcThreadPoolFactory implements ThreadFactory {

    private final String        namePrefix;
    private final boolean       isDaemon;
    private final ThreadGroup   group;
    private static final AtomicInteger pollNum = new AtomicInteger(1);
    private static final AtomicInteger threadNum = new AtomicInteger(1);
    public RpcThreadPoolFactory() {
        this("thread-pool");
    }

    public RpcThreadPoolFactory(String namePrefix) {
        this(namePrefix,false);
    }

    public RpcThreadPoolFactory(String namePrefix, boolean isDaemon) {
        this.isDaemon = isDaemon;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-" + pollNum.getAndIncrement() +"-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group,r,namePrefix+threadNum.getAndIncrement(),0);
        thread.setDaemon(isDaemon);
        //设置线程优先级
        if(thread.getPriority() != Thread.NORM_PRIORITY){
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
