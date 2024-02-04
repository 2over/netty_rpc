package com.cover.rpcnetty.rpc.server;

import com.cover.rpcnetty.rpc.base.RegisterService;
import com.cover.rpcnetty.rpc.base.vo.MessageType;
import com.cover.rpcnetty.rpc.base.vo.MyHeader;
import com.cover.rpcnetty.rpc.base.vo.MyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务处理类
 * channelRead0方法中有了实际的业务处理，负责具体的业务方法的调用
 * @author xieh
 * @date 2024/02/04 21:34
 */
@Service
@ChannelHandler.Sharable
public class ServerBusiHandler extends SimpleChannelInboundHandler<MyMessage> {

    private static final Log LOG
            = LogFactory.getLog(ServerBusiHandler.class);

    @Autowired
    private RegisterService registerService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) throws Exception {
        LOG.info(msg);
        MyMessage message = new MyMessage();
        MyHeader myHeader = new MyHeader();
        myHeader.setSessionID(msg.getMyHeader().getSessionID());
        myHeader.setType(MessageType.SERVICE_RESP.value());
        message.setMyHeader(myHeader);

        Map<String, Object> content = (HashMap<String,Object>)msg.getBody();
        // 方法所在类名接口名
        String serviceName = (String) content.get("siName");
        // 方法的名字
        String methodName = (String) content.get("methodName");
        Class<?>[] paramTypes = (Class<?>[]) content.get("paraTypes");
        //方法的入参的值
        Object[] args = (Object[]) content.get("args");
        // 从容器中拿到服务的Class对象
        Class serviceClass = registerService.getLocalService(serviceName);
        if (serviceClass == null) {
            throw new ClassNotFoundException(">>>>>>>" + serviceName + "not found");
        }

        // 通过反射，执行实际的服务
        Method method = serviceClass.getMethod(methodName, paramTypes);
        boolean result = (boolean) method.invoke(serviceClass, args);
        message.setBody(result);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.info(ctx.channel().remoteAddress() + "主动断开了连接");
//        super.channelInactive(ctx);
    }
}
