package com.zhanghe.rpc.core.server;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.channel.ServerChannelInitializer;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.threadpool.RpcThreadPool;
import com.zhanghe.threadpool.RpcThreadPoolFactory;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServerConnector {

  private static final Logger logger = LoggerFactory.getLogger(RpcServerConnector.class);

  private String ip;

  private int port;

  private EventLoopGroup bossGroup;

  private EventLoopGroup workerGroup;

  private ThreadPoolExecutor businessLogicExcutor;

  private ServerBootstrap bootstrap;

  private ServerChannelInitializer serverChannelInitializer;

  private ChannelFuture future ;

  private Serializer serializer;

  public RpcServerConnector() {
  }

  public RpcServerConnector(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public void init(){
    if( serializer == null ) {
      this.serverChannelInitializer = new ServerChannelInitializer();
    }else {
      this.serverChannelInitializer = new ServerChannelInitializer(serializer);
    }
    resetWorkGroup();
    serverChannelInitializer.setBusinessLogicExecutor(businessLogicExcutor);
    this.bootstrap = new ServerBootstrap();
    this.bootstrap.group(bossGroup, workerGroup)
        .channel(NettyEventLoopGroupUtil.getServerSocketChannelClass())
        .childHandler(serverChannelInitializer);
    this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
  }

  public boolean start() throws InterruptedException{
    this.future = this.bootstrap.bind(new InetSocketAddress(ip,port)).sync();
    future.addListener((f) -> {
      if (future.isSuccess()) {
        Channel channel = future.channel();
        System.out.println(channel.eventLoop());
      }
    });
    return this.future.isSuccess();
  }

  public void stop() throws InterruptedException{
    //todo 停止接收新的任务,等待现有任务完成
    businessLogicExcutor.shutdown();
    businessLogicExcutor.awaitTermination(100,TimeUnit.SECONDS);
    bossGroup.shutdownGracefully().sync();
    workerGroup.shutdownGracefully().sync();
  }

  public void resetWorkGroup(){
    businessLogicExcutor = RpcThreadPool.getExecutor(4, 1000);
    bossGroup = NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-server-boss")) ;
    workerGroup = NettyEventLoopGroupUtil.newEventLoopGroup(Runtime.getRuntime().availableProcessors()*2, new RpcThreadPoolFactory("Rpc-server-worker")) ;
    if (workerGroup instanceof NioEventLoopGroup) {
      ((NioEventLoopGroup) workerGroup).setIoRatio(50);
    }
  }

  public ServerChannelInitializer getServerChannelInitializer() {
    return serverChannelInitializer;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public Serializer getSerializer() {
    return serializer;
  }

  public void setSerializer(Serializer serializer) {
    this.serializer = serializer;
  }
}
