package com.zhanghe.protocol.v1.response;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.Packet;

public class RpcResponse extends Packet {

    public String RequestId;

    public boolean success;

    public Object result;

    public Exception exception;

    @Override
    public Byte getCommand() {
        return CommandType.RPC_RESPONSE;
    }

    @Override
    public boolean needSerilize() {
        return true;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "RequestId='" + RequestId + '\'' +
                ", result=" + result +
                ", exception=" + exception +
                '}';
    }
}
