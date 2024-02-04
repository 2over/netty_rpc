package com.cover.rpcnetty.rpc.base;

import com.cover.rpcnetty.remote.SendSms;
import com.cover.rpcnetty.rpc.base.vo.NettyConstant;
import com.cover.rpcnetty.rpc.server.ServerInit;
import com.cover.rpcnetty.rpc.sms.SendSmsImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * RPC框架的服务端部分，交给Spring托管
 * 包括Netty组件的初始化，监听端口，实际服务的注册等等
 *
 * @author xieh
 * @date 2024/02/04 21:07
 */
@Service
public class RpcServerFrame implements Runnable{
    @Autowired
    private RegisterService registerService;

    @Autowired
    private ServerInit serverInit;

    private static final Log LOG = LogFactory.getLog(RpcServerFrame.class);
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();


    public void bind() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(serverInit);

        // 绑定端口，同步等待成功
        b.bind(NettyConstant.REMOTE_PORT).sync();
        LOG.info("网络服务已准备好，可以进行业务操作了........:"
                + (NettyConstant.REMOTE_IP + ":" + NettyConstant.REMOTE_PORT));
    }
    @Override
    public void run() {
        try {
            bind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void startNet() {
        registerService.regService(SendSms.class.getName(), SendSmsImpl.class);
        new Thread(this).start();
    }

    @PreDestroy
    public void stopNet() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
    }


}
