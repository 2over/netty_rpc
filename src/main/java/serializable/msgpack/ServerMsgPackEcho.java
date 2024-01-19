package serializable.msgpack;

import constant.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;

public class ServerMsgPackEcho {

    public static void main(String[] args) throws InterruptedException {
        ServerMsgPackEcho serverMsgPackEcho = new ServerMsgPackEcho();
        System.out.println("服务器即将启动....");
        serverMsgPackEcho.start();
    }
    
    
    public void start() throws InterruptedException {
        MsgPackServerHandler serverHandler = new MsgPackServerHandler();
        // 线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 服务端启动必须
            ServerBootstrap b = new ServerBootstrap();
            b.group(group) // 将线程组传入
                    .channel(NioServerSocketChannel.class) // 指定使用NIO进行网络传输
                    .localAddress(new InetSocketAddress(Constant.DEFAULT_PORT)) // 指定服务器监听端口
                    // 服务端每接收到一个连接请求，就会新启一个socket通信，也就是channel
                    // 所以下面这段代码地作用就是为这个子channel增加handler
                    .childHandler(new ChannelInitializerImp());
            // 异步绑定到服务器, sync()会阻塞直到成功
            ChannelFuture f = b.bind().sync();
            // 给绑定事件增加一个监听器
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("绑定端口成功.....");
                }
            });

            System.out.println("服务器启动完成,等待客户端的连接和数据....");
            // 阻塞直到服务器的channel关闭
            ChannelFuture closeFuture = f.channel().closeFuture().sync();
            // 给关闭事件增加一个监听器
            closeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("服务器已关闭");
                }
            });

        } finally {
            // 优雅地关闭线程组
            group.shutdownGracefully().sync();
        }
    }
    
    private static class ChannelInitializerImp extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,
                    0,
                    2,
                    0,
                    2));
            
            ch.pipeline().addLast(new MsgPackDecoder());
            ch.pipeline().addLast(new MsgPackServerHandler());
        }
    }
}
