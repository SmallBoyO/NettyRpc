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
			//超时时间设置为10秒
			condition.await(10*1000,TimeUnit.MILLISECONDS);
			if(response!=null){
				return response.result;
			}else{
				return null;
			}
		}finally{
			lock.unlock();
		}
	}
	
	public void over(RpcResponse reponse) {
        try {
            lock.lock();
            condition.signal();
            this.response = reponse;
        } finally {
            lock.unlock();
        }
    }

}
