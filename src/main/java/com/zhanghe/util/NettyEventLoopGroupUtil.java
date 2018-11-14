package com.zhanghe.util;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ThreadFactory;

public class NettyEventLoopGroupUtil {

    /**
     * 查看是否可以使用Epoll
     */
    private static boolean epollEnabled = Epoll.isAvailable();

    public static EventLoopGroup newEventLoopGroup(int nThreads, ThreadFactory factory){
        return epollEnabled  ? new EpollEventLoopGroup(nThreads,factory) : new NioEventLoopGroup(nThreads,factory);
    }

    public static Class getServerSocketChannelClass(){
        return epollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

}
