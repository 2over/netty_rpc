package adv;

import adv.server.ServerInit;
import constant.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
    
    public void bind() throws InterruptedException {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1,
                new DefaultThreadFactory("boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors(),
                new DefaultThreadFactory("nt_worker"));
        
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ServerInit());
        
        // 绑定端口，同步等待成功
        ChannelFuture channelFuture = b.bind(Constant.DEFAULT_PORT).sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("绑定成功。。。。" + future.toString());
            }
        });
        
        LOG.info("Netty server start :" + Constant.DEFAULT_SERVER_IP + "  :" + Constant.DEFAULT_PORT);
        
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().bind();
    }
    
}
  