package com.zhanghe.rpc;

import com.zhanghe.ThreadPool.RpcThreadPoolFactory;
import com.zhanghe.channel.ClientChannelInitializer;
import com.zhanghe.channel.ServerChannelInitializer;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.protocol.serializer.impl.JsonSerializer;
import com.zhanghe.protocol.serializer.impl.KyroSerializer;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final EventLoopGroup workerGroup = NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-client-boss")) ;

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
        ChannelFuture future = bootstrap.connect().sync();
        this.proxy = new RpcRequestProxy(future.channel());
    }

    private RpcRequestProxy proxy;

    public Object proxy(String serviceName) throws ClassNotFoundException{
        logger.info("Rpc客户端代理接口:{}",serviceName);
        Class<?> clazz = Class.forName(serviceName);
        return Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{ clazz },
                proxy
        );
    }
}
