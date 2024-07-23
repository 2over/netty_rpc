package splicing.delimiter;

import constant.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.net.InetSocketAddress;

public class DelimiterEchoClient {
    
    private final String host;
    
    public DelimiterEchoClient(String host) {
        this.host = host;
    }
    
    public void start() throws InterruptedException {
        // 线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)// 将线程组传入
                    .channel(NioSocketChannel.class) // 使用指定NIO进行网络传输
                    .remoteAddress(new InetSocketAddress(host, Constant.DEFAULT_PORT)) //配置要连接服务器的ip地址和端口 
                    .handler(new ChannelInitializerImp());
            
            ChannelFuture f = b.connect().sync();
            System.out.println("已连接到服务器....");
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully().sync();
        }
        
    }
    
    private static class ChannelInitializerImp extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            ByteBuf delimiter = 
                    Unpooled.copiedBuffer(DelimiterEchoServer.DELIMITER_SYMBOL.getBytes());
            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
            channel.pipeline().addLast(new DelimiterClientHandler());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new DelimiterEchoClient(Constant.DEFAULT_SERVER_IP).start();
    }
}
