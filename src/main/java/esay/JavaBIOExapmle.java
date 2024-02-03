package esay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xieh
 * @date 2024/02/01 22:56
 */
public class JavaBIOExapmle {

    public static void main(String[] args) throws IOException {
        // 创建一个新的ServerSocket，用以监听指定端口上的连接请求，
        ServerSocket serverSocket = new ServerSocket(2222);
        // 对accept()方法的调用将被阻塞，直到一个连接建立
        Socket clientSocket = serverSocket.accept();
        // 这些流对象都派生于该套接字的流对象
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

        String request = null;
        String response = null;
        // 处理循环开始
        while ((request = in.readLine()) != null) {
            // 如果客户端发送了Done则退出处理循环
            if ("Done".equals(request)) {
                break;
            }

            // 请求被传递给服务器的处理方法
            response = processRequest(request);
            // 服务器的响应被发送给了客户端
            out.println(response);
            // 继续执行处理循环
        }
    }

    private static String processRequest(String request) {
        return null;
    }

}
