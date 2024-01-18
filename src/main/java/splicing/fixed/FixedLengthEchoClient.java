package splicing.fixed;

import constant.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.net.InetSocketAddress;

public class FixedLengthEchoClient {
    
    public static final String REQUEST = "Mark,zhuge,zhouyu,fox,loulan";
    
    private final String host;
    
    public FixedLengthEchoClient(String host) {
        this.host = host;
    }
    
    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端启动必须
            Bootstrap b = new Bootstrap();
            b.group(group)// 将线程组传入
                    .channel(NioSocketChannel.class) // 指定使用NIO进行网络传输
                    .remoteAddress(new InetSocketAddress(host, Constant.DEFAULT_PORT))
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
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new FixedLengthFrameDecoder(FixedLengthEchoServer.RESPONSE.length()));
            ch.pipeline().addLast(new FixedLengthClientHandler());
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new FixedLengthEchoClient(Constant.DEFAULT_SERVER_IP).start();
    }
}
