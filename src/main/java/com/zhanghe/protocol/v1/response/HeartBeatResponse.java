package com.zhanghe.protocol.v1.response;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.BasePacket;

public class HeartBeatResponse extends BasePacket {

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
