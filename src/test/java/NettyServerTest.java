import adv.NettyServer;
import org.junit.Test;

public class NettyServerTest {
    
    @Test
    public void test() throws InterruptedException {
        new NettyServer().bind();
    }
}
