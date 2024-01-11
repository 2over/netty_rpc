package splicing.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class EchoCliStickyHalfHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    private AtomicInteger counter = new AtomicInteger(0);
    
    /**
     * 客户端读取到网络数据后的处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("client Accept[" + byteBuf.toString(CharsetUtil.UTF_8) +
                "] and the counter is :" + counter.incrementAndGet()); 
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String request = "Mark,Zhuge,Fox,Zhouyu,Loulan" 
                + System.getProperty("line.separator");

        ByteBufAllocator alloc = ctx.alloc();
        ByteBuf msg = null;
        // 我们希望服务器接收到100个这样的报文
        for (int i = 0; i < 100; i++) {
            ByteBuf byteBuf = alloc.buffer();
            msg = alloc.buffer(request.length());
            msg.writeBytes(request.getBytes());
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
