package adv.server.asyncpro;

import adv.vo.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTaskProcessor implements ITaskProcessor{
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskProcessor.class);
    @Override
    public Runnable execAsyncTask(MyMessage msg) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOG.info("DefaultTaskProcessor模拟任务处理:" +  msg.getBody());
            }
        };
        return task;
    }
}
