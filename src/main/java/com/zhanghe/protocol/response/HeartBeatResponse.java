package com.zhanghe.protocol.response;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.Packet;

public class HeartBeatResponse extends Packet {

    @Override
    public Byte getCommand() {
        return CommandType.HEART_BEAT_RESPONSE;
    }

    @Override
    public boolean needSerilize() {
        return false;
    }
}
