package bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSingle {
    public static void main(String[] args) throws IOException {
        // 服务端启动必备
        ServerSocket serverSocket = new ServerSocket();
        // 表示服务端在哪个端口上监听
        serverSocket.bind(new InetSocketAddress(8888));
        System.out.println("Start Server....");
        int connectCount = 0;
        
        try {
            while(true) {
                System.out.println("一轮循环");
                Socket socket = serverSocket.accept();
                System.out.println("accept client socket .... total = " + (++connectCount));

                try (
                        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ) {

                    // 接收客户端的输出，也就是服务端的输入
                    String userName = inputStream.readUTF();
                    System.out.println("Accept Client message:" + userName);
                    // 服务器的输出，也就是客户端的输入
                    outputStream.writeUTF("Hello" + userName);
                    outputStream.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    socket.close();
                }
            }            
        } finally {
            serverSocket.close();
        }

    }
}
