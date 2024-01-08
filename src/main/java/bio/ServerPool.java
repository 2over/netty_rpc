package bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerPool {
    
    private static final Integer CPU_NUM = Runtime.getRuntime().availableProcessors();
    
    private static final Integer CORE_SIZE = CPU_NUM;
    
    private static final Integer MAX_SIZE = CPU_NUM * 2;

    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 0L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), (Runnable r) -> {
        Thread t = new Thread(r);
        t.setName("bio");
        return t;
    }, new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws IOException {
        // 服务端启动必备
        ServerSocket serverSocket = new ServerSocket();
        // 表示服务端在哪个端口上监听
        serverSocket.bind(new InetSocketAddress(8888));
        System.out.println("Start Server....");
        try {
            while (true) {
                pool.execute(new ServerTask(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }
    
    private static class ServerTask implements Runnable {
        private Socket socket = null;

        public ServerTask(Socket socket ) {
            this.socket = socket;
        }
        @Override
        public void run() {
            // 实例化与客户端通信的输入输出流
            try (
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ) {
                
                // 接受客户端的输出，也就是服务端的输入
                String userName = inputStream.readUTF();
                System.out.println("Accept Client Message:" + userName);
                // 服务器的输出,也就是客户端的输入
                outputStream.writeUTF("Hello, " + userName);
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
