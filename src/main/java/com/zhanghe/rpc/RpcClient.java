package com.zhanghe.rpc;

import com.zhanghe.ThreadPool.RpcThreadPoolFactory;
import com.zhanghe.channel.ClientChannelInitializer;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    private static final EventLoopGroup workerGroup = NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-client-boss"));;

    public void start(){
        if(stared.compareAndSet(false,true)){
            logger.info("Ready start RpcClient toconnect {}:{}.",serverIp,serverPort);
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
        SerializerManager.setDefault(SerializerAlgorithm.JSON);
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .remoteAddress(new InetSocketAddress(serverIp, serverPort));
    }

    public void doStart() throws InterruptedException{
        connect();
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
                    channel.closeFuture().addListener((closeFuture) -> {
                        //当channel断开
                        logger.debug("client disdonnect.ready toreconnetc!");
                        //修改 proxy的状态为断线
                        try {
                            proxyLock.lock();
                            proxy.getServerConnected().getAndSet(false);
                        }finally {
                            proxyLock.unlock();
                        }
                        //重连
                        connect();
                    });
                    try {
                        //修改prox代理所使用的的channel
                        if(proxy == null) {
                            this.proxy = new RpcRequestProxy();
                            this.proxy.connect(channel);
                        }else{
                            this.proxy.connect(channel);
                        }
                        proxyLock.lock();
                        proxyCondition.signalAll();
                    }finally {
                        proxyLock.unlock();
                    }
                    logger.debug("connect to server success.");
                } else {
                    logger.debug("connect to server failed,cause:{}.", f.cause().getMessage());
                    //连接失败之后 休眠一段时间重连
                    sleepSomeTime(1000);
                    connect();
                }
            });
        }finally {
            proxyLock.unlock();
        }
    }

    private RpcRequestProxy proxy;

    public Object proxy(String serviceName) throws ClassNotFoundException{
        try{
            proxyLock.lock();
            if (proxy != null && proxy.getServerConnected().get()){
                //如果已经连接上服务端
                logger.info("Rpc客户端代理接口:{}",serviceName);
                Class<?> clazz = Class.forName(serviceName);
                return Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        new Class<?>[]{ clazz },
                        proxy
                );
            }else{
                proxyCondition.await();
                logger.info("Rpc客户端代理接口:{}",serviceName);
                Class<?> clazz = Class.forName(serviceName);
                return Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        new Class<?>[]{ clazz },
                        proxy
                );
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            proxyLock.unlock();
        }
    }

    private void sleepSomeTime(long times){
        try {
            Thread.sleep(1000);
        } catch (Exception s) {

        }
    }
}
