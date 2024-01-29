package adv.server.asyncpro;

import io.netty.util.NettyRuntime;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncBusinessProcess {
    
    private static ExecutorService executorService = 
            new ThreadPoolExecutor(1,
                    NettyRuntime.availableProcessors(),
                    60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3000));
    
    
    public static void submitTask(Runnable task) {
        executorService.execute(task);
    }
}
