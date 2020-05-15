package com.zhanghe.rpc.core.client;

import com.zhanghe.channel.ClientChannelInitializer;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.v1.request.GetRegisterServiceRequest;
import com.zhanghe.threadpool.RpcThreadPoolFactory;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private EventLoopGroup workerGroup;

    private Client client;

    private Serializer serializer;

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
        bootstrap = new Bootstrap();
        if( serializer == null ){
            bootstrap.handler(new ClientChannelInitializer());
        }else{
            bootstrap.handler(new ClientChannelInitializer(serializer));
        }
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
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
        workerGroup.shutdownGracefully().sync();
    }

    public void connect(){
        logger.debug("ready connect to server {}:{}.",serverIp,serverPort);
            ChannelFuture future = bootstrap.connect();
            future.addListener((f) -> {
                if (future.isSuccess()) {
                    //连接成功后 在断开连接之后绑定重新连接的逻辑
                    Channel channel = future.channel();
                    activeChannel = channel;
                    client.setChannel(channel.remoteAddress().toString(),channel);
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
        client.setServices(activeChannel.remoteAddress().toString(),services);
    }

    public AtomicBoolean getGetServices() {
        return getServices;
    }

    public void setGetServices(AtomicBoolean getServices) {
        this.getServices = getServices;
    }

    public Channel getActiveChannel() {
        return activeChannel;
    }

    public void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public void resetWorkGroup(){
        workerGroup =  NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-client-boss"));;
    }

    private void sleepSomeTime(long times){
        try {
            Thread.sleep(1000);
        } catch (Exception s) {

        }
    }
}
