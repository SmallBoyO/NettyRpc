package com.zhanghe.protocol.v1.response;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.BasePacket;

public class RpcResponse extends BasePacket {

    public String requestId;

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
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
                "requestId='" + requestId + '\'' +
                ", result=" + result +
                ", exception=" + exception +
                '}';
    }
}
