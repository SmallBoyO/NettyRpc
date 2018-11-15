package com.zhanghe.protocol.response;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.Packet;

public class HeartBeatResponse extends Packet {

    public static final HeartBeatResponse INSTANCE = new HeartBeatResponse();

    @Override
    public Byte getCommand() {
        return CommandType.HEART_BEAT_RESPONSE;
    }

    @Override
    public boolean needSerilize() {
        return false;
    }
}
