package splicing.linebase;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class LineBaseServerHandler extends ChannelInboundHandlerAdapter {
    
    
    private AtomicInteger counter = new AtomicInteger(0);

    
    // 服务端接收到客户端连接时
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端:[" + ctx.channel().remoteAddress() + "] 已连接......");
//        super.channelActive(ctx);
    }

    
    // 服务端读取到网络数据后的处理
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        String request = in.toString(CharsetUtil.UTF_8);
        System.out.println("Server Accept [" + request + "] and the counter is :" + counter.incrementAndGet());
        
        String resp = "Hello ," + request + ". Welcome to Netty World!" 
                + System.getProperty("line.separator"); 
        
        ctx.writeAndFlush(Unpooled.copiedBuffer(resp.getBytes()));
//        super.channelRead(ctx, msg);
    }

    // 服务端读取完成网络数据后的处理
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        super.channelReadComplete(ctx);
        System.out.println("channelReadComplete...............");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
//        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "即将关闭....");
    }
}
