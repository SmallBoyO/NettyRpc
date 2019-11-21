package com.zhanghe.protocol.v1.request;

import com.zhanghe.protocol.v1.Command;
import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.BasePacket;

public class GetRegisterServiceRequest extends BasePacket {

  public static final GetRegisterServiceRequest INSTANCE = new GetRegisterServiceRequest();

  @Override
  public Byte getCommand() {
    return CommandType.GET_REGISTER_SERVICE_REQUEST;
  }

  @Override
  public boolean needSerilize() {
    return false;
  }
}
