package com.cover.rpcnetty.rpc.server;

import com.cover.rpcnetty.rpc.base.vo.MessageType;
import com.cover.rpcnetty.rpc.base.vo.MyHeader;
import com.cover.rpcnetty.rpc.base.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登陆检查
 * @author xieh
 * @date 2024/02/04 21:34
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    private final static Log LOG
                = LogFactory.getLog(LoginAuthRespHandler.class);

    // 用以检查用户是否重复登陆的缓存
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    // 用户登录的白名单
    private String[] whiteList = {"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        MyMessage message = (MyMessage) msg;
        // 如果是握手请求消息，处理，其他消息，透传
        if (message.getMyHeader() != null
            && message.getMyHeader().getType()
                == MessageType.LOGIN_REQ.value()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            MyMessage loginResp = null;
            // 重复登录,拒绝
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse((byte) -1);
            } else {
                // 检查用户是否在白名单中，在则允许登录，并写入缓存
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();

                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String WIP : whiteList) {
                    if (WIP.equals(ip)) {
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildResponse((byte) 0) : buildResponse((byte) -1);
                if (isOK) {
                    nodeCheck.put(nodeIndex, true);
                }
            }

            LOG.info("The login response is :" + loginResp
                    + " body [" + loginResp.getBody() + " ]");
            ctx.writeAndFlush(loginResp);
            ReferenceCountUtil.release(msg);
        }
        // 注释后，可延时消息不往下传递的情况
        else {
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        // 删除缓存
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);

    }

    private MyMessage buildResponse(byte result) {
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setType(MessageType.LOGIN_RESP.value());
        message.setMyHeader(myHeader);
        message.setBody(result);
        return message;
    }
}
