package serializable.msgpack;

import constant.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.net.InetSocketAddress;

public class ClientMsgPackEcho {
    
    private final String host;
    
    public ClientMsgPackEcho(String host) {
        this.host = host;
    }
    
    public void start() throws InterruptedException {
        // 线程组
        EventLoopGroup group = new NioEventLoopGroup();
        
        try {
            // 客户端启动必须
            Bootstrap b = new Bootstrap();
            b.group(group) // 将线程组传入
                    .channel(NioSocketChannel.class) // 指定使用NIO进行网络传输
                    // 配置需要连接的服务器的ip地址和端口
                    .remoteAddress(new InetSocketAddress(host, Constant.DEFAULT_PORT))
                    
                    .handler(new ChannelInitializerImp());

            ChannelFuture f = b.connect().sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("operationComplete..... ");
                }
            });
            System.out.println("已连接到服务器");

            ChannelFuture closeFuture = f.channel().closeFuture().sync();
            closeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("已关闭....");
                }
            });
        } finally {
            group.shutdownGracefully().sync();
        }
    }
    
    
    private static class ChannelInitializerImp extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            // 告诉Netty，计算一下报文的长度，然后作为报文头加在前面
            ch.pipeline().addLast(new LengthFieldPrepender(2));
            // 对服务器的应答也要解码，解决粘包半包
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            
            // 对我们要发送的数据做编码 -- 序列化
            ch.pipeline().addLast(new MsgPackEncode());
            ch.pipeline().addLast(new MsgPackClientHandler(5));
            
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ClientMsgPackEcho(Constant.DEFAULT_SERVER_IP).start();
    }
    
}
