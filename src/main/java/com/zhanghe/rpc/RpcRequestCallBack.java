package com.zhanghe.rpc;


import com.zhanghe.protocol.v1.response.RpcResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * rpc请求回调
 *
 * @author zhanghe
 */
public class RpcRequestCallBack {

    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private String requestId;

    private RpcResponse result;

    public RpcRequestCallBack(String requestId) {
        this.requestId = requestId;
    }

    public RpcResponse start(){
        try{
            lock.lock();
            if(result != null){
                return  result;
            }else{
                //阻塞住 直到收到服务端rpc请求
                condition.await(10*1000,TimeUnit.MILLISECONDS);
                if(result!=null){
                    //删除此次rpc调用的request
                    RpcRequestCallBackholder.callBackMap.remove(requestId);
                    return result;
                }else{
                    //删除此次rpc调用的request
                    RpcRequestCallBackholder.callBackMap.remove(requestId);
                    return null;
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
            result = new RpcResponse();
            result.setException(e);
            return result;
        }finally {
            lock.unlock();
        }
    }

    public void callBack(RpcResponse rpcResponse){
        try{
            lock.lock();
            result = rpcResponse;
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

}
