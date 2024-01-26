package http;

import constant.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;

public class HttpServer {
    // 通过nio方式来接收连接和处理连接
    private static EventLoopGroup group = new NioEventLoopGroup();
    
    // 服务端引导类
    private static ServerBootstrap b = new ServerBootstrap();
    
    // 是否开启SSL模式
    public static final boolean SSL = true;

    // Netty创建全部都是实现自AbstractBootstrap,客户端的是Bootstrap,服务端的则是ServerBootstrap
    public static void main(String[] args) throws Exception {
        final SslContext sslContext;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(),
                    ssc.privateKey()).build();
            
        } else {
            sslContext = null;
        }
        
        try {
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    // 设置过滤器
                    .childHandler(new ServerHandlerInit(sslContext));
            // 异步进行绑定
            ChannelFuture f = b.bind(Constant.DEFAULT_PORT);
            // 给ChannelFuture 增加监听器
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("绑定端口已成功....");
                }
            });
            System.out.println("服务端启动成功，端口是:" + Constant.DEFAULT_PORT);
            System.out.println("服务器启动模式: " + (SSL ? "SSL安全模式" : "普通模式"));
            // 监听服务器关闭监听
            ChannelFuture closeFuture = f.channel().closeFuture().sync();
            closeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("服务器已经关闭....");
                }
            });
        } finally {
            // 关闭EventLoopGroup,释放掉所有资源，包括创建的线程
            group.shutdownGracefully();
        }
    }
}
