package com.zhanghe.protocol.v1.response;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.Packet;
import java.util.Set;

public class GetRegisterServiceResponse extends Packet {

  public Set<String> services;

  @Override
  public Byte getCommand() {
    return CommandType.GET_REGISTER_SERVICE_RESPONSE;
  }

  @Override
  public boolean needSerilize() {
    return true;
  }

  public Set<String> getServices() {
    return services;
  }

  public void setServices(Set<String> services) {
    this.services = services;
  }

}
