package adv.server;

import adv.server.asyncpro.AsyncBusinessProcess;
import adv.server.asyncpro.ITaskProcessor;
import adv.vo.EncryptUtils;
import adv.vo.MessageType;
import adv.vo.MsgHeader;
import adv.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerBusinessHandler extends SimpleChannelInboundHandler<MyMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerBusinessHandler.class);

    private ITaskProcessor taskProcessor;

    public ServerBusinessHandler(ITaskProcessor taskProcessor) {
        super();
        this.taskProcessor = taskProcessor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) throws Exception {
        // 检查MD5
        String headMd5 = msg.getMsgHeader().getMd5();
        String calcMd5 = EncryptUtils.encryptObj(msg.getBody());
        if (!headMd5.equals(calcMd5)) {
            LOG.error("报文MD5检查不通过:" + headMd5 + "vs" + calcMd5 + ", 关闭连接");
            ctx.writeAndFlush(buildBusinessResp("报文MD5检查不通过，关闭连接"));
            ctx.close();
        }
        LOG.info(msg.toString());

        if (msg.getMsgHeader().getType() == MessageType.ONE_WAY.value()) {
            LOG.debug("ONE_WAY类型消息，异步处理");
            AsyncBusinessProcess.submitTask(taskProcessor.execAsyncTask(msg));
        } else {
            LOG.debug("TWO_WAY类型消息，应答");
            ctx.writeAndFlush(buildBusinessResp("OK"));
        }

    }

    private MyMessage buildBusinessResp(String result) {
        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.SERVICE_RESP.value());
        myMessage.setMsgHeader(msgHeader);
        myMessage.setBody(result);
        return myMessage;
    }
}
