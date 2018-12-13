package org.nocoder.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

// Sharable 标识这类的实例之间可以在 channel 里面共享
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        /*
         * ChannelHandlerContext 对象提供了许多操作，使你能够触发各种各样的 I/O 事件和操作。
         * 这里我们调用了 write(Object) 方法来逐字地把接受到的消息写入。
         * 请注意不同于 DISCARD 的例子我们并没有释放接受到的消息，
         * 这是因为当写入的时候 Netty 已经帮我们释放了。
         */
        channelHandlerContext.write(msg);
        /*
         * ctx.write(Object) 方法不会使消息写入到通道上，他被缓冲在了内部，
         * 你需要调用 ctx.flush() 方法来把缓冲区中数据强行输出。
         * 或者你可以用更简洁的 cxt.writeAndFlush(msg) 以达到同样的目的。
         */
        channelHandlerContext.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 冲刷所有待审消息到远程节点。关闭通道后，操作完成
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}