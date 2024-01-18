package splicing.delimiter;

import constant.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.net.InetSocketAddress;

public class DelimiterEchoServer {
    
    public static final String DELIMITER_SYMBOL = "@~";

    public static void main(String[] args) throws InterruptedException {
        DelimiterEchoServer delimiterEchoServer = new DelimiterEchoServer();
        System.out.println("服务器即将启动");
        delimiterEchoServer.start();
    }
    
    public void start() throws InterruptedException {
        DelimiterServerHandler serverHandler = new DelimiterServerHandler();
        // 线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 服务端启动必须
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)// 线程组传入
                    .channel(NioServerSocketChannel.class) // 指定使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(Constant.DEFAULT_PORT)) //指定服务器监听端口
                    // 服务端没接收到一个连接请求，就会重启一个socket通信，也就是channel
                    // 所以下面这段代码的作用就是为这个子channel增加handler
                    .childHandler(new ChannelInitializerImp());

            ChannelFuture f = b.bind().sync();
            System.out.println("服务器启动完成，等待客户端的连接和数据....");
            // 阻塞直到服务器的channel关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅关闭线程组
            group.shutdownGracefully().sync();
        }
    }
    
    private static class ChannelInitializerImp extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER_SYMBOL.getBytes());
            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
            channel.pipeline().addLast(new DelimiterServerHandler());
        }
    }
}
