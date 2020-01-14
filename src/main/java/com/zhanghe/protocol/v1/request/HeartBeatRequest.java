package com.zhanghe.protocol.v1.request;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.BasePacket;
/**
 * 心跳请求包
 *
 * @author zhanghe
 */
public class HeartBeatRequest extends BasePacket {

    public static final HeartBeatRequest INSTANCE = new HeartBeatRequest();

    @Override
    public Byte getCommand() {
        return CommandType.HEART_BEAT_REQUEST;
    }

    /**
     * 心跳包不需要序列化
     * @return
     */
    @Override
    public boolean needSerilize() {
        return false;
    }
}
