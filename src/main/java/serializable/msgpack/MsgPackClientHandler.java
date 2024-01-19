package serializable.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MsgPackClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    private final int sendNumber;
    
    public MsgPackClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }
    
    private AtomicInteger counter = new AtomicInteger(0);
    
    // 客户端读取到网络数据后的处理
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("client Accept [" + msg.toString(CharsetUtil.UTF_8) +
                " ] and the counter is :" + counter.incrementAndGet());
    }

    // 客户端被通知channel活跃后，做事
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        User[] users = makeUsers();
        // 发送数据
        for (User user : users) {
            System.out.println("Send user :" + user);
            ctx.write(user);
        }
        
        ctx.flush();
    }

    // 发生异常后的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
    
    // 生成用户实体类的数组，以供发送
    private User[] makeUsers() {
        User[] users = new User[sendNumber];
        
        User user = null;
        for (int i = 0; i < sendNumber; i++) {
            user = new User();
            user.setAge(i);
            // 这里为了方便演示，我采用了一个随机数，用来表示这个报文不是定长的，而是可变的
            String userName = "ABCEDFG---->" + getSpecialSymbol() + i;
            user.setUserName(userName);
            user.setId("No:" + (sendNumber - i));
            user.setUserContact( new UserContact(userName + "@cover.com", "133"));
            users[i] = user;
        }
        
        return users;
    }
    
    private String getSpecialSymbol() {
        Random random = new Random();

        int count = random.nextInt(10);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append("*");
        }
        
        return stringBuilder.toString();
    }
}
