package http;

import constant.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpClient {

    public static final String HOST  = "127.0.0.1";
    public static void main(String[] args) throws InterruptedException {
        if (HttpServer.SSL) {
            System.out.println("服务器处于SSL模式，客户端不支持，推出");
            return ;
        }
        
        HttpClient client = new HttpClient();
        client.connect(Constant.DEFAULT_SERVER_IP, Constant.DEFAULT_PORT);
    }
    
    public void connect(String host, int port) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            
                            ch.pipeline().addLast(new HttpClientCodec());
                            // 聚合Http为一个完整的报文
                            ch.pipeline().addLast("aggregator",
                                    new HttpObjectAggregator(10 * 1024 * 1024));
                            // 解压缩
                            ch.pipeline().addLast("decompressor", new HttpContentDecompressor());
                            
                            ch.pipeline().addLast(new HttpClientInboundHandler());
                                    
                        }
                    });
            
            // start 
            ChannelFuture f = b.connect(host, port).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("连接成功....");
                }
            });

            ChannelFuture closeFuture = f.channel().closeFuture().sync();
            closeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("关闭成功...");
                }
            });
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    
    
    
}
