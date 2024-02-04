package com.cover.rpcnetty.rpc.client;

import com.cover.rpcnetty.rpc.base.vo.MessageType;
import com.cover.rpcnetty.rpc.base.vo.MyHeader;
import com.cover.rpcnetty.rpc.base.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 发起登录请求
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(LoginAuthReqHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        MyMessage message = (MyMessage) msg;
        // 如果是握手应答消息，需要判断是否认证成功
        if (message.getMyHeader() != null
                && message.getMyHeader().getType()
                == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte)0) {
                // 握手失败,关闭连接
                ctx.close();
            } else {
                LOG.info("Login is ok:" + message);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        ctx.fireExceptionCaught(cause);
    }

    private MyMessage buildLoginReq() {
        MyMessage myMessage = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.LOGIN_REQ.value());
        myMessage.setMyHeader(myHeader);
        return myMessage;
    }
}
