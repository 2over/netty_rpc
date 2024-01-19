package splicing.linebase;

import constant.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.net.InetSocketAddress;

public class LineBaseEchoClient {
    
    private final String host;
    
    public LineBaseEchoClient(String host) {
        this.host = host;
    }
    
    public void start() throws InterruptedException {
        // 线程组
        EventLoopGroup group = new NioEventLoopGroup(); 
        try {
            Bootstrap b = new Bootstrap();
            b.group(group) // 将线程组传入
                    .channel(NioSocketChannel.class) // 指定使用NIO进行网络传输
                    .remoteAddress(new InetSocketAddress(host, Constant.DEFAULT_PORT)) // 配置要连接服务器的
                    .handler(new ChannelInitializerImp());
            ChannelFuture f = b.connect().sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("operationComplete 已经连接到服务器了....");
                }
            });
            System.out.println("已连接到服务器....");
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully().sync();
        }
    }
    
    private static class ChannelInitializerImp extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new LineBaseClientHandler());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new LineBaseEchoClient(Constant.DEFAULT_SERVER_IP).start();
    }
}
