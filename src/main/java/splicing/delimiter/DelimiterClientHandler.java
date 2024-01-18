package splicing.delimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class DelimiterClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * 客户端读取到网络数据后的处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        System.out.println("client Accept[" + msg.toString(CharsetUtil.UTF_8) 
                + "] and the counter is :" + counter.incrementAndGet());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf msg = null;
        String request = "Mark,Zhuge,Zhouyu,fox,loulan" + DelimiterEchoServer.DELIMITER_SYMBOL;
//        super.channelActive(ctx);
        for (int i = 0; i < 100; i++) {
            
            msg = Unpooled.buffer(request.length());
            msg.writeBytes(request.getBytes());
            ctx.writeAndFlush(msg);
            System.out.println("发送数据到服务器");
        }
    }
}
