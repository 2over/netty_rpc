package adv.client;

import adv.NettyServer;
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

public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginAuthReqHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //发出认证请求
        MyMessage loginMsg = buildLoginReq();
        LOG.info("请求服务器认证:" + loginMsg);
        ctx.writeAndFlush(loginMsg);
//        super.channelActive(ctx);
    }

    private MyMessage buildLoginReq() {
        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.LOGIN_REQ.value());
        myMessage.setMsgHeader(msgHeader);
        return myMessage;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);

        MyMessage message = (MyMessage) msg;
        if (message.getMsgHeader() != null
                && message.getMsgHeader().getType() == MessageType.LOGIN_RESP.value()) {
            LOG.info("收到认证应答报文，服务器是否验证通过？....");
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte)0) {
                // 握手成功，关闭连接
                LOG.warn("未通过认证，关闭连接: " + message);
                ctx.close();
            } else {
                LOG.info("通过认证， 移除本处理器， 进入业务通信 ：" + message);
                ctx.pipeline().remove(this);
                ReferenceCountUtil.release(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
