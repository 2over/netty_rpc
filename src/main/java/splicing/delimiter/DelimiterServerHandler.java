package splicing.delimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class DelimiterServerHandler extends ChannelInboundHandlerAdapter {
    
    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * 服务端读取到网络数据后的处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        String request = in.toString(CharsetUtil.UTF_8);
        System.out.println("Server Accept[" + request 
                + "] and counter is :" + counter.incrementAndGet());
        
        String resp = "Hello," + request 
                + ". Welcome to Netty World!" 
                + DelimiterEchoServer.DELIMITER_SYMBOL;
        ctx.writeAndFlush(Unpooled.copiedBuffer(resp.getBytes()));
//        super.channelRead(ctx, msg);
    }

    /**
     * 服务端读取完成网络数据后的处理
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        super.channelReadComplete(ctx);

        System.out.println("channelReadComplete-------");
//        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
//        super.exceptionCaught(ctx, cause);
    }
}
