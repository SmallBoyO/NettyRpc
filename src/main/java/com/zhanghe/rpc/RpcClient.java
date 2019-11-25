package com.zhanghe.rpc;

import com.zhanghe.threadpool.RpcThreadPoolFactory;
import com.zhanghe.channel.ClientChannelInitializer;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.protocol.v1.request.GetRegisterServiceRequest;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rpc客户端
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private String serverIp;

    private int serverPort;

    private AtomicBoolean stared = new AtomicBoolean(false);

    public RpcClient(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    private Bootstrap bootstrap;

    private Channel activeChannel;

    private static final EventLoopGroup WORKER_GROUP = NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-client-boss"));;

    public void start(){
        if(stared.compareAndSet(false,true)){
            logger.info("Ready start RpcClient,connect address {}:{}.",serverIp,serverPort);
            try{
                init();
                doStart();
            }catch (Exception e){
                logger.info("ERROR:RpcClient started failed! Reason:{}",e);
                throw new IllegalStateException(e);
            }
        }else{
            String error = "ERROR:RpcClient already stared!";
            logger.error(error);
            throw new IllegalStateException(error);
        }
    }

    public void init() {
        SerializerManager.setDefault(SerializerAlgorithm.KYRO);
        bootstrap = new Bootstrap();
        bootstrap.group(WORKER_GROUP)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .remoteAddress(new InetSocketAddress(serverIp, serverPort));
        //注册退出事件
        Runtime.getRuntime().addShutdownHook(new Thread(()->
        {
            logger.debug("ShutdownHook execute start...");
            activeChannel.close();
            WORKER_GROUP.shutdownGracefully();
            logger.debug("ShutdownHook execute end...");
        },""));
    }

    public void doStart(){
       connect();
    }

    public void stop(){
        try {
            if(stared.compareAndSet(true,false)) {
                doStop();
            }else{
                String error = "ERROR:RpcClient already stop!";
                logger.error(error);
                throw new IllegalStateException(error);
            }
        }catch (Exception e){
            logger.info("ERROR:RpcClient stop failed! Reason:{}",e);
            throw new IllegalStateException(e);
        }
    }

    public void doStop() throws InterruptedException{
        WORKER_GROUP.shutdownGracefully().sync();
    }

    private Lock proxyLock = new ReentrantLock();

    private Condition proxyCondition = proxyLock.newCondition();

    public void connect(){
        logger.debug("ready connect to server.");
        try {
            proxyLock.lock();
            ChannelFuture future = bootstrap.connect();
            future.addListener((f) -> {
                if (future.isSuccess()) {
                    //连接成功后 在断开连接之后绑定重新连接的逻辑
                    Channel channel = future.channel();
                    activeChannel = channel;
                    channel.attr(AttributeKey.valueOf("rpcClient")).set(this);
                    //查询服务端接口列表
                    channel.writeAndFlush(GetRegisterServiceRequest.INSTANCE);

                    try {
                        proxyLock.lock();
                        //修改proxy代理所使用的的channel
                        if(proxy == null) {
                            this.proxy = new RpcRequestProxy();
                            this.proxy.connect(channel);
                        }else{
                            this.proxy.connect(channel);
                        }
                    }finally {
                        proxyLock.unlock();
                    }
                    logger.debug("connect to server success.");
                } else {
                    if(stared.get()){
                        logger.debug("connect to server failed,cause:{}.", f.cause().getMessage());
                        //连接失败之后 休眠一段时间重连
                        sleepSomeTime(1000);
                        connect();
                    }
                }
            });
        }finally {
            proxyLock.unlock();
        }
    }

    private RpcRequestProxy proxy;

    private Set<String> registeredServices;

    public Object proxy(String serviceName) throws ClassNotFoundException{
        try{
            proxyLock.lock();
            if (proxy == null || !proxy.getServerConnected().get()){
                //还没连接上服务端
                logger.info("Rpc客户端等待连接服务器");
                proxyCondition.await();
                logger.info("Rpc客户端连上服务器");
            }
            logger.info("Rpc客户端代理接口:{}",serviceName);
            return realProxy(serviceName);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            proxyLock.unlock();
        }
    }

    public void setRegisterServices(Set<String> services){
        try{
            proxyLock.lock();
            registeredServices = services;
            proxy.initServices();
            proxyCondition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            proxyLock.unlock();
        }
    }

    public Object realProxy(String serviceName) throws ClassNotFoundException{
        if(!registeredServices.contains(serviceName)){
            throw new RuntimeException("服务端未提供此service");
        }
        Class<?> clazz = Class.forName(serviceName);
        return Proxy.newProxyInstance(
            clazz.getClassLoader(),
            new Class<?>[]{ clazz },
            proxy
        );
    }

    private void sleepSomeTime(long times){
        try {
            Thread.sleep(1000);
        } catch (Exception s) {

        }
    }
}
