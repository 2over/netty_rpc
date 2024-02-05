package com.cover.rpcnetty.rpc.server;

import com.cover.rpcnetty.rpc.base.kryocodec.KryoDecoder;
import com.cover.rpcnetty.rpc.base.kryocodec.KryoEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务端Handler的初始化
 * 交给Spring托管，ServerBusiHandler用注入方式实例化后加入Netty的pipeline
 * @author xieh
 * @date 2024/02/04 21:11
 */
@Service
public class ServerInit extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ServerBusiHandler serverBusiHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Netty提供的日志打印Handler，可以展示发送接收出去的字节
        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));

        // 剥离接收到的消息的长度字段，拿到实际的消息报文的字节数组
        ch.pipeline().addLast("frameDecoder",
                new LengthFieldBasedFrameDecoder(65535,
                        0,2,
                        0,2));
        // 给发送出去的消息增加长度字段
        ch.pipeline().addLast("frameEncoder",
                new LengthFieldPrepender(2));
        // 反序列化,将字节数组转换为消息实体
        ch.pipeline().addLast(new KryoDecoder());
        // 序列化
        ch.pipeline().addLast("MessageEncoder", new KryoEncoder());
        // 超时检测
        ch.pipeline().addLast("readTimeoutHandler",
                new ReadTimeoutHandler(50));
        // 登陆应答
        ch.pipeline().addLast(new LoginAuthRespHandler());
        // 心跳应答
        ch.pipeline().addLast("HeartBeatHandler",
                new HeartBeatRespHandler());
        // 服务端业务处理
        ch.pipeline().addLast("ServerBusiHandler",
                serverBusiHandler);

    }
}
