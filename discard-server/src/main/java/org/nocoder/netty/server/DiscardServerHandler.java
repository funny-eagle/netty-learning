package org.nocoder.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        ByteBuf in = ((ByteBuf) msg);
        try{
            while(in.isReadable()){
                System.out.println((char) in.readByte());
                System.out.flush();
            }
        } finally {
            // Discard the received data silently.
            in.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("channel read complete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
