package adv.server.asyncpro;

import adv.vo.MyMessage;

// 消息转任务处理器
public interface ITaskProcessor {
    
    Runnable execAsyncTask(MyMessage msg);
}
