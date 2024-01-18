package splicing.fixed;

import constant.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.net.InetSocketAddress;

public class FixedLengthEchoServer {
    
    
    public static final String RESPONSE = "Welcome to Netty";

    public static void main(String[] args) throws InterruptedException {
        FixedLengthEchoServer fixedLengthEchoServer = new FixedLengthEchoServer();
        System.out.println("服务器即将启动");
        fixedLengthEchoServer.start();
    }
    
    public void start() throws InterruptedException {
        FixedLengthServerHandler serverHandler = new FixedLengthServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();// 线程组
        try {
            // 服务端启动必须
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)// 将线程组传入
                    .channel(NioServerSocketChannel.class) // 指定使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(Constant.DEFAULT_PORT)) // 指定服务器监听端口
                    // 服务端每接收到一个连接请求，就会新启一个Socket通信，也就是channel
                    // 所以下面这段代码的作用就是为这个子channel增加handler
                    .childHandler(new ChannelInitializerImp());

            // 异步绑定到服务器,sync()会阻塞直到完成
            ChannelFuture f = b.bind().sync();
            System.out.println("服务器启动完成,等待客户端的连接和数据.....");
            // 阻塞直到服务器的channel关闭
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
    
    private static class ChannelInitializerImp extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new FixedLengthFrameDecoder(FixedLengthEchoClient.REQUEST.length()));
            ch.pipeline().addLast(new FixedLengthServerHandler());
        }
    }
}
