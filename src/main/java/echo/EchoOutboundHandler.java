package echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

public class EchoOutboundHandler extends ChannelOutboundHandlerAdapter {


    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("EchoOutboundHandler begin write..............");
        super.write(ctx, msg, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        System.out.println("EchoOutboundHandler begin connect..............");
        super.connect(ctx, remoteAddress, localAddress, promise);
    }
}
