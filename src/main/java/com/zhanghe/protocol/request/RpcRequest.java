package com.zhanghe.protocol.request;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.Packet;

public class RpcRequest extends Packet {

    @Override
    public Byte getCommand() {
        return CommandType.RPC_REQUEST;
    }

    @Override
    public boolean needSerilize() {
        return true;
    }

}
