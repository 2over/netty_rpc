package com.cover.rpcnetty.rpc.client;

import com.cover.rpcnetty.rpc.RpcClientFrame;
import com.cover.rpcnetty.rpc.base.vo.MessageType;
import com.cover.rpcnetty.rpc.base.vo.MyHeader;
import com.cover.rpcnetty.rpc.base.vo.MyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ChannelHandler.Sharable
public class ClientBusiHandler extends SimpleChannelInboundHandler<MyMessage> {
    private static final Log LOG = LogFactory.getLog(ClientBusiHandler.class);
    
    private ChannelHandlerContext ctx;
    
    private final ConcurrentHashMap<Long, BlockingQueue<Object>> responseMap 
            = new ConcurrentHashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) throws Exception {
            if (msg.getMyHeader() != null && msg.getMyHeader().getType() == MessageType.SERVICE_RESP.value())  {
                long sessionId = msg.getMyHeader().getSessionID();
                boolean result = (boolean) msg.getBody();
                BlockingQueue<Object> msgQueue = responseMap.get(sessionId);
                msgQueue.put(result);
            }
    }
    
    public Object send(Object message) throws InterruptedException {
        if (ctx.channel() == null || !ctx.channel().isActive()) {
            throw new IllegalStateException("和服务器还未建立起有效连接! 请稍后再试!!");
        }

        MyMessage msg = new MyMessage();
        MyHeader myHeader = new MyHeader();
        Random r = new Random();
        long sessionId = r.nextLong() + 1;
        myHeader.setSessionID(sessionId);
        myHeader.setType(MessageType.SERVICE_REQ.value());
        msg.setMyHeader(myHeader);
        msg.setBody(message);
        BlockingQueue<Object> msgQueue = new ArrayBlockingQueue<>(1);
        responseMap.put(sessionId, msgQueue);
        ctx.writeAndFlush(msg);
        Object result = msgQueue.take();
        LOG.info("获取到服务端的处理结果" + result);
        return result;
    }
}
