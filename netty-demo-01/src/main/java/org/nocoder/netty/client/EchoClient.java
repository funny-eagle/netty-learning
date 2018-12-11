package org.nocoder.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 引导客户端
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();                // 创建bootstrap
            b.group(group)                                // 指定group来处理客户端事件
                    .channel(NioSocketChannel.class)      // 使用的channel类型用于nio传输
                    .remoteAddress(new InetSocketAddress(host, port))    // 设置服务器的 InetSocketAddress
                    .handler(new ChannelInitializer<SocketChannel>() {    // 添加 EchoClientHandler 实例 到 channel pipeline
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new EchoClientHandler());
                        }
                    });

            ChannelFuture f = b.connect().sync();        // 连接到远程，等待连接完成

            f.channel().closeFuture().sync();            // 阻塞直到channel关闭
        } finally {
            group.shutdownGracefully().sync();            // 优雅关闭线程池，释放所有资源
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoClient("127.0.0.1", 8000).start();
    }
}
