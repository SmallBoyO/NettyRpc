package com.zhanghe.rpc;

import io.netty.channel.Channel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelInfo {

  private Channel channel;

  private AtomicBoolean serverConnected = new AtomicBoolean(false);

  private AtomicBoolean servicesInited = new AtomicBoolean(false);

  private Lock channelLock = new ReentrantLock();

  public ChannelInfo(Channel channel) {
    this.channel = channel;
  }

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public AtomicBoolean getServerConnected() {
    return serverConnected;
  }

  public void setServerConnected(AtomicBoolean serverConnected) {
    this.serverConnected = serverConnected;
  }

  public AtomicBoolean getServicesInited() {
    return servicesInited;
  }

  public void setServicesInited(AtomicBoolean servicesInited) {
    this.servicesInited = servicesInited;
  }

  public Lock getChannelLock() {
    return channelLock;
  }

  public void setChannelLock(Lock channelLock) {
    this.channelLock = channelLock;
  }
}
