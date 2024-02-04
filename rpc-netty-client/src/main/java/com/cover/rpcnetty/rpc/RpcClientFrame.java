package com.cover.rpcnetty.rpc;

import com.cover.rpcnetty.rpc.base.vo.NettyConstant;
import com.cover.rpcnetty.rpc.client.ClientBusiHandler;
import com.cover.rpcnetty.rpc.client.ClientInit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * RPC框架的客户端代理部分，交给Spring托管
 * 1.动态代理的实现中，不再连接服务器，而是直接发送请求
 * 2.客户端网络部分的主题，包括Netty组件的初始化
 */
@Service
public class RpcClientFrame implements Runnable {
    
    private static final Log LOG = LogFactory.getLog(RpcClientFrame.class);
    
    private ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(1);
    
    private Channel channel;
    
    private EventLoopGroup group = new NioEventLoopGroup();
    
    // 是否用户主动关闭连接的标志值
    private volatile boolean userClose = false;
    // 连接是否成功关闭的标志值
    private volatile boolean connected = false;

    @Autowired
    private ClientInit clientInit;
    
    @Autowired
    private ClientBusiHandler clientBusiHandler;
    
    // 远程服务的代理对象，参数为客户端要调用的服务
    public <T> T getRemoteProxyObject(Class<?> serviceInterface) {
        // 拿到一个代理对象，由这个代理对象通过网络进行实际的服务调用
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new DynProxy(serviceInterface, clientBusiHandler));
    }
    
    // 动态代理，实现对远程服务的访问
    private static class DynProxy implements InvocationHandler {
        
        private Class<?> serviceInterface;
        
        private ClientBusiHandler clientBusiHandler;
        
        public DynProxy(Class<?> serviceInterface, ClientBusiHandler clientBusiHandler) {
            this.serviceInterface = serviceInterface;
            this.clientBusiHandler = clientBusiHandler;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Map<String, Object> content = new HashMap<>();
            content.put("siName", serviceInterface.getName());
            content.put("methodName", method.getName());
            content.put("paraTypes", method.getParameterTypes());
            content.put("args", args);
            return clientBusiHandler.send(content);
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    // 连接服务器
    public void connect(int port, String host) throws InterruptedException {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(clientInit);
            
            // 发起异步连接操作
            ChannelFuture future = b.connect(new InetSocketAddress(host, port)).sync();
            
            channel = future.sync().channel();
            // 连接成功后通知等待线程,连接已经建立
            synchronized (this) {
                this.connected = true;
                this.notifyAll();
            }
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!userClose) {
                // 非用户主动关闭，说明了网络问题，需要进行重连操作
                System.out.println("发现异常，可能发生了服务器异常或网络问题, 准备进行重连....." );
                
                // 再次发起重连
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            try {
                                connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                // 用户主动关闭，释放资源
                channel = null;
                group.shutdownGracefully().sync();
                connected = false;
            }
        }
    } 
    

    @Override
    public void run() {
        try {
            connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        userClose = true;
        channel.close();
    }
    
    @PostConstruct
    public void startNet() throws InterruptedException {
        new Thread(this).start();
        while (!this.isConnected()) {
            synchronized (this) {
                this.wait();
            }
        }
        
        LOG.info("网络通信已准备好，可以进行业务操作了............");
    }
    
    @PreDestroy
    public void stopNet() {
        close();
    }
}
