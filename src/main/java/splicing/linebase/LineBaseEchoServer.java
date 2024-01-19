package splicing.linebase;

import constant.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.net.InetSocketAddress;

public class LineBaseEchoServer {

    public static void main(String[] args) throws InterruptedException {
        LineBaseEchoServer lineBaseEchoServer = new LineBaseEchoServer();
        System.out.println("服务器即将启动");
        lineBaseEchoServer.start();
    }
    
    public void start() throws InterruptedException {
        LineBaseServerHandler serverHandler = new LineBaseServerHandler();
        
        EventLoopGroup group = new NioEventLoopGroup();// 线程组
        try {
            // 服务端启动必须
            ServerBootstrap b = new ServerBootstrap();
            b.group(group) // 将线程组传入
                    .channel(NioServerSocketChannel.class) // 指定使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(Constant.DEFAULT_PORT))// 指定服务器监听端口
                    // 服务端每接收到一个连接请求，就会新启一个socket通信，也就是channel
                    // 所以下面这段代码的作用就是为这个子channel增加handler
                    .childHandler(new ChannelInitializerImp());


            // 异步绑定到服务器，sync()会阻塞直到完成
            ChannelFuture f = b.bind().sync();
            // 我们可以为这个ChannelFuture增加监听器，当它有回调结果的时候通知channelFutureListener
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("服务器端绑定已经完成");
                }
            });

            System.out.println("服务器启动完成,等待客户端的连接和数据......");
            // 阻塞直到服务器的channel关闭
            f.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully().sync();
        }
    }
    
    private static class ChannelInitializerImp extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new LineBaseServerHandler());
        }
    }
}
