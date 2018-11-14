package com.zhanghe.rpc;

import com.zhanghe.channel.ServerChannelInitializer;
import com.zhanghe.util.NettyEventLoopGroupUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;

/**
 * Rpc客户端
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private String serverIp;

    private int serverPort;

    public RpcClient(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void init() throws InterruptedException{
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NettyEventLoopGroupUtil.getServerSocketChannelClass())
                .handler(new ServerChannelInitializer());
        ChannelFuture future = bootstrap.bind(serverIp,serverPort);
        future.sync();
        logger.debug("连接服务端成功");
    }
}
