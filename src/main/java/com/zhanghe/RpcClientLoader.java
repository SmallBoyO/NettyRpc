package com.zhanghe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zhanghe.handler.ClientHandler;

public class RpcClientLoader {

	private volatile static RpcClientLoader rpcClientLoader;
	
	private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
	
	public ClientHandler clientHandler;
	
	//等待Netty服务端链路建立通知信号
    private Lock lock = new ReentrantLock();
    
    private Condition signal = lock.newCondition();

	public RpcClientLoader(){
		
	}
	
	public static RpcClientLoader getInstance(){
		if(rpcClientLoader==null){
			synchronized (RpcClientLoader.class) {
				if(rpcClientLoader==null){
					rpcClientLoader = new RpcClientLoader();
				}
			}
		}
		return rpcClientLoader;
	}

	public ClientHandler getClientHandler() throws InterruptedException {
		try{
			lock.lock();
			if(this.clientHandler==null){
				signal.await();
			}
			return this.clientHandler;
		}finally{
			lock.unlock();
		}
	}

	public void setClientHandler( ClientHandler clientHandler ) {
		try{
			lock.lock();
			this.clientHandler = clientHandler;
			signal.signalAll();
		}finally{
			lock.unlock();
		}
	}
	
	
}
