package http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class ServerHandlerInit extends ChannelInitializer<SocketChannel> {
    
    private final SslContext sslContext;
    
    public ServerHandlerInit(SslContext sslContext) {
        this.sslContext = sslContext;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }
        
        // pipeline中的handler可以自定义名称方便排查问题
        // 把应答报文 编码
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // 把请求报文 解码
        pipeline.addLast("decoder", new HttpRequestDecoder());
        
        // 聚合http为一个完整的报文
        pipeline.addLast("aggregator",
                new HttpObjectAggregator(10*1024*1024));
        // 把应答报文压缩
        pipeline.addLast("compressor", new HttpContentCompressor());
        pipeline.addLast(new BusinessHandler());
    }
}
