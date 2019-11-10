package com.zhanghe.rpc;

import com.zhanghe.ThreadPool.RpcThreadPoolFactory;
import com.zhanghe.channel.ServerChannelInitializer;
import com.zhanghe.channel.hanlder.server.BindRpcServiceHandler;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.service.TestServiceImpl;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Rpc服务端启动程序
 *
 * @author zhanghe
 */
public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private AtomicBoolean stared = new AtomicBoolean(false);

    private String ip;

    private int port;

    public RpcServer(int port) {
       this("127.0.0.1",port);
    }

    public RpcServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start(){
        if(stared.compareAndSet(false,true)){
            logger.info("Ready start RpcServer on port {}.",port);
            try{
                doInit();
                doStart();
            }catch (Exception e){
                logger.error("ERROR:RpcServer started failed.reason:{}",e.getMessage());
                throw new IllegalStateException(e);
            }
            logger.info("RpcServer started on port {}.",port);
        }else{
            String error = "ERROR:RpcServer already stared!";
            logger.error(error);
            throw new IllegalStateException(error);
        }
    }

    private static final EventLoopGroup bossGroup = NettyEventLoopGroupUtil.newEventLoopGroup(1, new RpcThreadPoolFactory("Rpc-server-boss")) ;

    private static final EventLoopGroup workerGroup = NettyEventLoopGroupUtil.newEventLoopGroup(Runtime.getRuntime().availableProcessors()*2, new RpcThreadPoolFactory("Rpc-server-worker")) ;

    //设置即I/O操作和用户自定义任务的执行时间比
    static {
        if (workerGroup instanceof NioEventLoopGroup) {
            ((NioEventLoopGroup) workerGroup).setIoRatio(50);
        } else if (workerGroup instanceof EpollEventLoopGroup) {
            ((EpollEventLoopGroup) workerGroup).setIoRatio(50);
        }
    }

    private ServerBootstrap bootstrap;

    public void doInit(){
        SerializerManager.setDefault(SerializerAlgorithm.KYRO);
        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(bossGroup,workerGroup)
                .channel(NettyEventLoopGroupUtil.getServerSocketChannelClass())
//                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ServerChannelInitializer());
        this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.bootstrap.childHandler(ServerChannelInitializer.INSTANCE);
    }

    private ChannelFuture future ;

    public boolean doStart() throws InterruptedException{
        this.future = this.bootstrap.bind(new InetSocketAddress(ip,port));
        future.sync();
        return this.future.isSuccess();
    }

    public void bind(Object obj){
        BindRpcServiceHandler.INSTANCE.getServiceMap().put(obj.getClass().getInterfaces()[0].getName(), obj);
    }
}
