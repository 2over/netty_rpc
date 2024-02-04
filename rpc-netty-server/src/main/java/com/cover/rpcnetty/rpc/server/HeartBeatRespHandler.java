package com.cover.rpcnetty.rpc.server;

import com.cover.rpcnetty.rpc.base.vo.MessageType;
import com.cover.rpcnetty.rpc.base.vo.MyHeader;
import com.cover.rpcnetty.rpc.base.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 心跳应答
 * @author xieh
 * @date 2024/02/04 21:34
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    private static final Log LOG
   			= LogFactory.getLog(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;
        // 返回心跳应答消息
        if (message.getMyHeader() != null && message.getMyHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            LOG.info("Receive client heart beat message : ----->");
            MyMessage heartBeat = buildHeartBeat();
            LOG.info("Send heart beat response message to : ----->");
            ctx.writeAndFlush(heartBeat);
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
//        super.channelRead(ctx, msg);
    }

    private MyMessage buildHeartBeat() {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.HEARTBEAT_RESP.value());
        message.setMyHeader(myHeader);
        return message;
    }
}
