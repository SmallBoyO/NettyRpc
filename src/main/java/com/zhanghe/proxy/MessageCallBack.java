package com.zhanghe.proxy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zhanghe.protocol.RpcRequest;
import com.zhanghe.protocol.RpcResponse;

public class MessageCallBack {

	public RpcRequest request;
	public RpcResponse response;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public MessageCallBack( RpcRequest request ){
		super();
		this.request = request;
	}
	
	public Object start() throws InterruptedException{
		try{
			lock.lock();
			if(response!=null){
				return response.result;
			}else{
				//超时时间设置为10秒 调用Condition的await()方法，会使当前线程进入等待队列并释放锁，同时线程状态变为等待状态。
				condition.await(11*1000,TimeUnit.MILLISECONDS);
				if(response!=null){
					return response.result;
				}else{
					return null;
				}
			}
		}finally{
			lock.unlock();
		}
	}
	
	public void over(RpcResponse reponse) {
        try {
            lock.lock();
            this.response = reponse;
			//调用Condition的signal()方法，将会唤醒在等待队列中等待时间最长的节点（首节点），在唤醒节点之前，会将节点移到同步队列中。
			//Condition的signalAll()方法，相当于对等待队列中的每个节点均执行一次signal()方法，将等待队列中的节点全部移动到同步队列中，并唤醒每个节点的线程。
			condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
