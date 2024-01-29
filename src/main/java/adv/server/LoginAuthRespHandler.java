package adv.server;

import adv.vo.MessageType;
import adv.vo.MsgHeader;
import adv.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class LoginAuthRespHandler  extends ChannelInboundHandlerAdapter {
    
    
    private static final Logger LOG = LoggerFactory.getLogger(LoginAuthRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        MyMessage message = (MyMessage) msg;
        // 是不是握手认证请求
        if (message.getMsgHeader() != null 
            && message.getMsgHeader().getType() == MessageType.LOGIN_REQ.value()) {
            LOG.info("收到客户端认证请求 :" + message);
            String nodeIndex = ctx.channel().remoteAddress().toString();
            MyMessage loginResp = null;
            boolean checkAutuPass = false;
            // 重复登录，拒绝，这里用客户端的地址代替了实际的用户信息
            if (SecurityCenter.isDupLog(nodeIndex)) {
                loginResp = buildResponse((byte) -1);
                LOG.warn("拒绝重复登录, 应答消息:" + loginResp);
                ctx.writeAndFlush(loginResp);
                ctx.close();
            } else {
                // 检查用户是否在白名单中，在则允许登录，并写入缓存
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                if (SecurityCenter.isWhiteIP(ip)) {
                    SecurityCenter.addLoginUser(nodeIndex);
                    loginResp = buildResponse((byte) 0);
                    LOG.info("认证通过，应答消息:" + loginResp);
                    ctx.writeAndFlush(loginResp);
                } else {
                    loginResp = buildResponse((byte) -1);
                    LOG.warn("认证失败, 应答信息 :" + loginResp);
                    ctx.writeAndFlush(loginResp);
                    ctx.close();
                }
            }
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private MyMessage buildResponse(byte result) {
        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.LOGIN_RESP.value());
        myMessage.setMsgHeader(msgHeader);
        myMessage.setBody(result);
        return myMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        SecurityCenter.removeLoginUser(ctx.channel().remoteAddress().toString());
        ctx.close();
    }
}
