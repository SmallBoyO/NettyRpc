package com.zhanghe.protocol.v1.response;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.BasePacket;

public class RpcResponse extends BasePacket {

    public String requestId;

    public boolean success;

    public Object result;

    public String exceptionMessage;

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
            "requestId='" + requestId + '\'' +
            ", success=" + success +
            ", result=" + result +
            ", exceptionMessage='" + exceptionMessage + '\'' +
            '}';
    }
}
