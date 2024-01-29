package adv.server;

import adv.kryocodec.KryoDecoder;
import adv.kryocodec.KryoEncoder;
import adv.server.asyncpro.DefaultTaskProcessor;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ServerInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 战报半包问题
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,
                0, 2,0,2));
        
        ch.pipeline().addLast(new LengthFieldPrepender(2));
        
        // 序列化相关
        ch.pipeline().addLast(new KryoDecoder());
        ch.pipeline().addLast(new KryoEncoder());
        // 处理心跳超时
        ch.pipeline().addLast(new ReadTimeoutHandler(15));

        ch.pipeline().addLast(new LoginAuthRespHandler());
        ch.pipeline().addLast(new HeartBeatRespHandler());

        ch.pipeline().addLast(new ServerBusinessHandler(new DefaultTaskProcessor()));
        
    }
}
