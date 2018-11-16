package com.zhanghe.rpc;

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
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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
        SerializerManager.setDefault(SerializerAlgorithm.JSON);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        InetSocketAddress socketaddress = new InetSocketAddress(serverIp, serverPort);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .remoteAddress(socketaddress);
        ChannelFuture future = bootstrap.connect().sync();
        logger.debug("连接服务端成功");
    }
}
