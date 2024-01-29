package adv.client;

import adv.vo.MessageType;
import adv.vo.MsgHeader;
import adv.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// 客户端在长久未向服务器业务请求时，发出心跳请求报文
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            MyMessage heartBeat = buildHeartBeat();
            LOG.debug("写空闲,发出心跳报文维持连接: " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        }
        super.userEventTriggered(ctx, evt);
    }

    private MyMessage buildHeartBeat() {
        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.HEARTBEAT_REQ.value());
        myMessage.setMsgHeader(msgHeader);
        return myMessage;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        MyMessage message = (MyMessage)msg;
        if (message.getMsgHeader() != null
                && message.getMsgHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            LOG.debug("收到服务器心跳应答， 服务器正常");
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            LOG.warn("服务器长时间未应答， 关闭链路");
        }
        super.exceptionCaught(ctx, cause);
    }
}
