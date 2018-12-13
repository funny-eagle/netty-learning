package org.nocoder.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {
    private int port;

    public DiscardServer(int port){
        this.port = port;
    }

    public void run() throws Exception{
        /*
         * 1. NioEventLoopGroup 是用来处理IO操作的多线程事件循环器
         *  boss 用来接受进来的连接，worker 用来处理意见被接受的连接
         *  一旦 boss 接受到连接，就会把连接信息注册到worker上
          */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            /*
             * 2.ServerBootstrap 是一个启动NIO服务的辅助启动类
             * 可以在这个服务中直接使用Channel
             *
             */
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            // 3 这里我们指定使用 NioServerSocketChannel类来举例说明一个新的Channel如何接收进来的连接
            bootstrap.channel(NioServerSocketChannel.class);
            // 4
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new DiscardServerHandler());
                }
            });
            // 5
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            // 6
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // Bind and start to accept incoming connections
            // 7
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            // wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully shut down your server
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        // 服务启动后，使用 telnet 命令测试
        new DiscardServer(8000).run();
    }

}
