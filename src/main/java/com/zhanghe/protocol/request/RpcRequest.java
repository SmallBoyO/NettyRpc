package com.zhanghe.protocol.request;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.Packet;

public class RpcRequest extends Packet {

    public String requestId;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public Byte getCommand() {
        return CommandType.RPC_REQUEST;
    }

    @Override
    public boolean needSerilize() {
        return true;
    }

}
