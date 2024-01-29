package adv.server;

import adv.vo.MessageType;
import adv.vo.MsgHeader;
import adv.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatRespHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        MyMessage message = (MyMessage)msg;
        // 是不是心跳请求
        if (message.getMsgHeader() != null 
                && message.getMsgHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            // 心跳应答报文
            MyMessage heartBeatResp = buildHeartBeat();
            LOG.debug("心跳应答: " + heartBeatResp);
            ctx.writeAndFlush(heartBeatResp);
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private MyMessage buildHeartBeat() {
        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.HEARTBEAT_RESP.value());
        myMessage.setMsgHeader(msgHeader);
        return myMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            LOG.warn("客户端长时间未通信，可能已经宕机，关闭链路");
            SecurityCenter.removeLoginUser(ctx.channel().remoteAddress().toString());
            ctx.close();
        }

        super.exceptionCaught(ctx, cause);
    }
}
