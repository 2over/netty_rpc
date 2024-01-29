package adv;

import adv.client.ClientInit;
import adv.vo.*;
import constant.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);

    // 负责重连的线程池
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    
    private Channel channel;
    
    private EventLoopGroup group = new NioEventLoopGroup();
    
    // 是否用户主动关闭连接的标志
    private volatile boolean userClose = false;
    
    // 连接是否成功关闭的标志
    private volatile boolean connected = false;
    
    public boolean isConnected() {
        return connected;
    }
    
    public void connect(int port, String host) throws InterruptedException {
        try {
            // 客户端启动必备
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class) // 指定使用NIO的通信模式
                    .handler(new ClientInit());
            ChannelFuture future = b.connect(new InetSocketAddress(host, port)).sync();
            LOG.info("已连接服务器");
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    LOG.info("连接事件产生回调....operationCompletable");
                }
            });

            channel = future.channel();
            synchronized (this) {
                this.connected = true;
                this.notifyAll();
            }

            channel.closeFuture().sync();            
        } finally {
            if (!userClose) {
                // 非正常关闭，有可能发生了网络问题
                LOG.warn("需要进行重连");
                
                executorService.execute(() -> {
                    try {
                        // 给操作系统足够的时间取释放相关的资源
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
//                        throw new RuntimeException(e);
                    }
                });
            } else {
                // 正常关闭
                channel = null;
                group.shutdownGracefully().sync();
                synchronized (this) {
                    this.connected = false;
                    this.notifyAll();
                }
            }
        }

    }
    @Override
    public void run() {
        try {
            connect(Constant.DEFAULT_PORT, Constant.DEFAULT_SERVER_IP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void sendOneWay(Object message) throws IllegalAccessException {
        if (channel == null || !channel.isActive()) {
            throw new IllegalAccessException("和服务器还没建立起有效连接，请稍后再试");
        }

        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        
        msgHeader.setMsgId(MakeMsgId.getID());
        msgHeader.setType(MessageType.ONE_WAY.value());
        msgHeader.setMd5(EncryptUtils.encryptObj(message));
        myMessage.setMsgHeader(msgHeader);
        myMessage.setBody(message);
        
        channel.writeAndFlush(myMessage);
    }

    public void send(Object message) throws IllegalAccessException {
        if (channel == null || !channel.isActive()) {
            throw new IllegalAccessException("和服务器还没建立起有效连接，请稍后再试");
        }

        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();

        msgHeader.setMsgId(MakeMsgId.getID());
        msgHeader.setType(MessageType.SERVICE_REQ.value());
        msgHeader.setMd5(EncryptUtils.encryptObj(message));
        myMessage.setMsgHeader(msgHeader);
        myMessage.setBody(message);

        channel.writeAndFlush(myMessage);
    }
    
    public void close() {
        userClose = true;
        channel.close();
    }
}
