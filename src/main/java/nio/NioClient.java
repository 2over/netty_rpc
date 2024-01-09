package nio;

import constant.Constant;

import java.io.IOException;
import java.util.Scanner;

public class NioClient {
    
    private static NioClientHandle nioClientHandle;
    
    public static void start() {
        nioClientHandle = new NioClientHandle(Constant.DEFAULT_SERVER_IP, Constant.DEFAULT_PORT);
        
        new Thread(nioClientHandle, "Client").start();
    }
    
    // 向服务器发送消息
    public static boolean sendMsg(String msg) throws IOException {
        nioClientHandle.sendMsg(msg);
        return true;
    }

    public static void main(String[] args) throws IOException {
        start();
        Scanner scanner = new Scanner(System.in);
        while (NioClient.sendMsg(scanner.next()));
    }
}
