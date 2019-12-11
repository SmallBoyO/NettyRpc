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

/**
 * Rpc客户端
 */
public class RpcClientConnector {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientConnector.class);

    private String serverIp;

    private int serverPort;

    private AtomicBoolean stared = new AtomicBoolean(false);

    private AtomicBoolean getServices = new AtomicBoolean(false);

    public RpcClientConnector(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    private Bootstrap bootstrap;

    private Channel activeChannel;

    private EventLoopGroup WORKER_GROUP;

    private RpcClientHolder rpcClientHolder;

    public void start(){
        if(stared.compareAndSet(false,true)){
            logger.info("Ready start RpcClientConnector,connect address {}:{}.",serverIp,serverPort);
            try{
                init();
                doStart();
            }catch (Exception e){
                logger.info("ERROR:RpcClientConnector started failed! Reason:{}",e);
                throw new IllegalStateException(e);
            }
        }else{
            String error = "ERROR:RpcClientConnector already stared!";
            logger.error(error);
            throw new IllegalStateException(error);
        }
    }

    public void init() {
        resetWorkGroup();
        SerializerManager.setDefault(SerializerAlgorithm.KYRO);
        bootstrap = new Bootstrap();
        bootstrap.group(WORKER_GROUP)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .remoteAddress(new InetSocketAddress(serverIp, serverPort));
    }

    public void doStart(){
       connect();
    }

    public void stop(){
        try {
            if(stared.compareAndSet(true,false)) {
                doStop();
            }else{
                String error = "ERROR:RpcClientConnector already stop!";
                logger.error(error);
                throw new IllegalStateException(error);
            }
        }catch (Exception e){
            logger.info("ERROR:RpcClientConnector stop failed! Reason:{}",e);
            throw new IllegalStateException(e);
        }
    }

    public void doStop() throws InterruptedException{
        WORKER_GROUP.shutdownGracefully().sync();
    }

    public void connect(){
        logger.debug("ready connect to server.");
            ChannelFuture future = bootstrap.connect();
            future.addListener((f) -> {
                if (future.isSuccess()) {
                    //连接成功后 在断开连接之后绑定重新连接的逻辑
                    Channel channel = future.channel();
                    activeChannel = channel;
                    rpcClientHolder.setChannel(channel.remoteAddress().toString(),channel);
                    channel.attr(AttributeKey.valueOf("rpcClient")).set(this);
                    //查询服务端接口列表
                    channel.writeAndFlush(GetRegisterServiceRequest.INSTANCE);
                    channel.closeFuture().addListener((closeFuture) -> {
                        //当channel断开
                        logger.debug("client disconnect.ready to reconnect!");
                        //重连
                        connect();
                    });
                    //修改proxy代理所使用的的channel
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
    }

    private Set<String> registeredServices;

    public void setRegisterServices(Set<String> services){
        this.getServices.set(true);
        rpcClientHolder.setServices(activeChannel.remoteAddress().toString(),services);
    }

    public AtomicBoolean getGetServices() {
        return getServices;
    }

    public void setGetServices(AtomicBoolean getServices) {
        this.getServices = getServices;
    }

    public RpcClientHolder getRpcClientHolder() {
        return rpcClientHolder;
    }

    public Channel getActiveChannel() {
        return activeChannel;
    }

    public void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
    }

    public void setRpcClientHolder(RpcClientHolder rpcClientHolder) {
        this.rpcClientHolder = rpcClientHolder;
    }

    public void resetWorkGroup(){
        WORKER_GROUP =  NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-client-boss"));;
    }

    private void sleepSomeTime(long times){
        try {
            Thread.sleep(1000);
        } catch (Exception s) {

        }
    }
}
