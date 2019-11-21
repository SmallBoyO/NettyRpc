package com.zhanghe.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcServerThreadFactory implements ThreadFactory {
	
	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	private final ThreadGroup threadGroup;
	
	private final boolean daemoThread;
	
	
	public RpcServerThreadFactory(){
		super();
		SecurityManager s = System.getSecurityManager();
        threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        daemoThread = true;
	}



	@Override
	public Thread newThread( Runnable r ) {
		String name = "RpcThread" + mThreadNum.getAndIncrement();
        Thread ret = new Thread(threadGroup, r, name, 0);
        ret.setDaemon(daemoThread);
        return ret;
	}

}
