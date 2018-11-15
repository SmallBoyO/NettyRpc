package com.zhanghe.protocol.v1;


public enum Command {
    HEART_BEAT_REQUEST("心跳发送包",(byte)1),
    HEART_BEAT_RESPONSE("心跳回复包",(byte)2);


    private String name;

    private Byte command;

    Command(String name,byte command) {
        this.name = name;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getCommand() {
        return command;
    }

    public void setCommand(Byte command) {
        this.command = command;
    }
}
