package com.cover.rpcnetty.rpc.client;

import com.cover.rpcnetty.rpc.base.kryocodec.KryoDecoder;
import com.cover.rpcnetty.rpc.base.kryocodec.KryoEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 客户端Handler的初始化
 * 交给Spring托管，clientBusiHandler用注入方式实例化化加入Netty的pipeline
 */
@Service
public class ClientInit extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ClientBusiHandler clientBusiHandler;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 剥离接收到的消息的长度字段，拿到实际的消息保温的字节数组
        ch.pipeline().addLast("frameDecoder",
                new LengthFieldBasedFrameDecoder(65535,
                        0,2,0,2));
        // 给发送出去的消息增加长度字段
        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
        // 反序列化，将字节数组转换为消息实体
        ch.pipeline().addLast(new KryoDecoder());
        // 序列化,将消息尸体转换为字节数组准备进行网络传输
        ch.pipeline().addLast("MessageEncoder", new KryoEncoder());
        // 超时检测
        ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(10));
        // 发出登录请求
        ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
        // 发出心跳请求
        ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
        // 业务处理
        ch.pipeline().addLast("ClientBusiHandler", clientBusiHandler);
    }
}
