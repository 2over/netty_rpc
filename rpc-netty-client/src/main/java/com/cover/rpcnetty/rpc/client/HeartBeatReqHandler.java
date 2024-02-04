package com.cover.rpcnetty.rpc.client;

import com.cover.rpcnetty.rpc.base.vo.MessageType;
import com.cover.rpcnetty.rpc.base.vo.MyHeader;
import com.cover.rpcnetty.rpc.base.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ScheduledFuture;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 心跳请求处理
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(HeartBeatReqHandler.class);

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        MyMessage message = (MyMessage) msg;
        // 握手或者说登陆成功，主动发送心跳消息
        if (message.getMyHeader() != null && message.getMyHeader().getType()
                == MessageType.LOGIN_RESP.value()) {
            heartBeat = ctx.executor()
                    .scheduleAtFixedRate(new HeartBeatReqHandler
                                    .HeartBeatTask(ctx),
                    0, 5000, TimeUnit.MILLISECONDS);

            ReferenceCountUtil.release(msg);
        } else if (message.getMyHeader() != null
                && message.getMyHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            // 如果是心跳应答
            LOG.info("Client receive server heart beat message : ----->");
            ReferenceCountUtil.release(msg);
        } else {
            // 如果是其他报文，传播给后面的Handler
            ctx.fireChannelRead(msg);
        }
    }


    private class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext ctx;

        // 心跳计数，可用可不用，已经有超时处理机制
        private final AtomicInteger heartBeatCount;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx =ctx;
            heartBeatCount = new AtomicInteger(0);
        }

        @Override
        public void run() {
            MyMessage heartBeat = buildHeartBeat();
            ctx.writeAndFlush(heartBeat);
        }

        private MyMessage buildHeartBeat() {
            MyMessage message = new MyMessage();
            MyHeader myHeader = new MyHeader();
            myHeader.setType(MessageType.HEARTBEAT_REQ.value());
            message.setMyHeader(myHeader);
            return message;
        }
    }

}
