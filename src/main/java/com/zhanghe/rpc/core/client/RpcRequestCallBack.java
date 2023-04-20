package com.zhanghe.rpc.core.client;


import com.zhanghe.protocol.v1.response.RpcResponse;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

    private Boolean done;

    private Boolean cancel;

    public RpcRequestCallBack(String requestId) {
        this.requestId = requestId;
        done = false;
        cancel = false;
    }

    public RpcResponse get(Long timeOut,TimeUnit timeUnit) throws TimeoutException{
        try{
            lock.lock();
            if(result != null){
                RpcRequestCallBackholder.callBackMap.remove(requestId);
                return  result;
            }else{
                //阻塞住 直到收到服务端rpc请求
                if(timeOut!=null && timeUnit!=null) {
                    condition.await(timeOut, timeUnit);
                    if(result == null){
                        //到时间了还没得到返回
                        throw new TimeoutException("time Out");
                    }
                }else{
                    condition.await();
                }
                if(result!=null){
                    //删除此次rpc调用的request
                    RpcRequestCallBackholder.callBackMap.remove(requestId);
                    return result;
                }else{
                    //删除此次rpc调用的request
                    RpcRequestCallBackholder.callBackMap.remove(requestId);
                    throw new TimeoutException("rpc request timeout!");
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
            result = new RpcResponse();
            result.setExceptionMessage(e.getMessage());
            return result;
        }finally {
            lock.unlock();
        }
    }

    public RpcResponse start() throws TimeoutException {
        return get(10 * 1000L,TimeUnit.SECONDS);
    }

    public void callBack(RpcResponse rpcResponse){
        try{
            lock.lock();
            result = rpcResponse;
            done = true;
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public Boolean isCancel() {
        return cancel;
    }

    public Boolean cancel() {
        try{
            lock.lock();
            if(!done){
                this.cancel = true;
                result = new RpcResponse();
                result.setExceptionMessage("Rpc request has been canceled!");
                condition.signalAll();
                return true;
            }else{
                return false;
            }
        }finally {
            lock.unlock();
        }
    }

    public Boolean isDone() {
        return done;
    }
}
