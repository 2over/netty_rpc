package splicing.fixed;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class FixedLengthClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    private AtomicInteger counter = new AtomicInteger(0);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("client Accept [" + msg.toString(CharsetUtil.UTF_8)
                + "] and the counter is :" + counter.incrementAndGet());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf msg = null;
        for (int i = 0; i < 100; i++) {
            msg = Unpooled.buffer(FixedLengthEchoClient.REQUEST.length());
            msg.writeBytes(FixedLengthEchoClient.REQUEST.getBytes());
            ctx.writeAndFlush(msg);
        }
//        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
//        super.exceptionCaught(ctx, cause);
    }
}
