package com.zhanghe.protocol;


/**
 *
 */
public abstract class Packet {
    /**
     * 协议版本
     */
    public Byte version = 1;

    /**
     * 指令名
     * @return
     */
    public abstract Byte getCommand();

    /**
     * 是否需要序列化类
     * @return
     */
    public abstract boolean needSerilize();

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }
}
