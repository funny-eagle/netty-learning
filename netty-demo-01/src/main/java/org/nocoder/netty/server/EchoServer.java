package org.nocoder.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        // 设置端口
        int port = 8000;
        // 调用服务器的start()方法
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        // 创建 NioEventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建 serverBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    // 指定使用NIO的传输Channel
                    .channel(NioServerSocketChannel.class)
                    // 设置 socket 地址使用所选的端口
                    .localAddress(new InetSocketAddress(port))
                    // 添加 EchoServerHandler 到Chanel 的ChannelPipline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new EchoServerHandler());
                        }
                    });

            // 等待服务器关闭
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            // 关闭channel和块
            f.channel().closeFuture().sync();
        } finally {
            // 关闭EventLoopGroup 释放所有资源
            group.shutdownGracefully().sync();
        }
    }

}