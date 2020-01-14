package com.zhanghe.rpc.core.server;

import com.zhanghe.channel.ServerChannelInitializer;
import com.zhanghe.config.RpcConfig;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.threadpool.RpcThreadPoolFactory;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRpcServer implements Server {

  private static final Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

  private AtomicBoolean started = new AtomicBoolean(false);

  private String ip;

  private int port;

  private EventLoopGroup BOSS_GROUP;

  private EventLoopGroup WORKER_GROUP;

  private ServerBootstrap bootstrap;

  private ServerChannelInitializer serverChannelInitializer;

  private ChannelFuture future ;

  public AbstractRpcServer() {
    this.serverChannelInitializer = new ServerChannelInitializer();
  }

  public AbstractRpcServer(int port) {
    this.ip = RpcConfig.DEFAULT_IP;
    this.port = port;
    this.serverChannelInitializer = new ServerChannelInitializer();
  }

  public AbstractRpcServer(String ip, int port) {
    this.ip = ip;
    this.port = port;
    this.serverChannelInitializer = new ServerChannelInitializer();
  }

  @Override
  public void init() {
    logger.info("Server ready to init");
    if(started.compareAndSet(false,true)){
      logger.info("Ready start Server on port {}.",port);
      try{
        doInit();
        doStart();
      }catch (Exception e){
        started.set(false);
        logger.error("ERROR:Server started failed.reason:{}",e.getMessage());
        throw new RuntimeException(e);
      }
      logger.info("Server started on port {}.",port);
    }else{
      String error = "ERROR:Server already started!";
      logger.error(error);
      throw new RuntimeException(error);
    }
    logger.info("Server init success");
  }

  private void doInit(){
    resetWorkGroup();
    SerializerManager.setDefault(SerializerAlgorithm.KYRO);
    this.bootstrap = new ServerBootstrap();
    this.bootstrap.group(BOSS_GROUP, WORKER_GROUP)
        .channel(NettyEventLoopGroupUtil.getServerSocketChannelClass())
        .childHandler(serverChannelInitializer);
    this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
  }

  public boolean doStart() throws InterruptedException{
    this.future = this.bootstrap.bind(new InetSocketAddress(ip,port)).sync();
    return this.future.isSuccess();
  }

  @Override
  public void destroy() {
    logger.info("Server ready to destroy");
    stop();
    logger.info("Server destroy success");
  }

  public void doStop() throws InterruptedException{
    BOSS_GROUP.shutdownGracefully().sync();
    WORKER_GROUP.shutdownGracefully().sync();
  }

  public void stop(){
    try{
      if(started.getAndSet(false)) {
        doStop();
      }else{
        String error = "ERROR:Server not started!";
        logger.error(error);
        throw new IllegalStateException(error);
      }
      logger.info("Server {}:{} stop success.",ip,port);
    }catch (Exception e){
      logger.error("ERROR:Server stop failed.reason:{}",e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void bind(Object service){
    logger.info("bind service:"+service.getClass().getName());
    serverChannelInitializer.getBindRpcServiceHandler().getServiceMap().put(service.getClass().getInterfaces()[0].getName(), service);
  }

  @Override
  public void bind(List<Object> services){
    services.forEach(service -> {
      logger.info("bind service:" + service.getClass().getInterfaces()[0].getName());
      serverChannelInitializer.getBindRpcServiceHandler().getServiceMap().put(service.getClass().getInterfaces()[0].getName(), service);
    });
  }

  public void resetWorkGroup(){
    BOSS_GROUP = NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-server-boss")) ;
    WORKER_GROUP = NettyEventLoopGroupUtil.newEventLoopGroup(Runtime.getRuntime().availableProcessors()*2, new RpcThreadPoolFactory("Rpc-server-worker")) ;
    if (WORKER_GROUP instanceof NioEventLoopGroup) {
      ((NioEventLoopGroup) WORKER_GROUP).setIoRatio(50);
    } else if (WORKER_GROUP instanceof EpollEventLoopGroup) {
      ((EpollEventLoopGroup) WORKER_GROUP).setIoRatio(50);
    }
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

  public List<Object> getServices() {
    return null;
  }

  public void setServices(List<Object> services) {
    bind(services);
  }
}
