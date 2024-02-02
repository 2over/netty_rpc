package rpc.reg.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import remote.vo.RegisterServiceVo;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 服务注册中心，服务提供者在启动时需要注册中心登记自己的信息
@Component
public class RegisterCenter {
    // key表示服务名，value代表
    private static final Map<String, Set<RegisterServiceVo>> serviceHolder 
            = new HashMap<>();
    // 注册服务端口号
    private int port;

    /**
     * 服务注册，考虑到可能有多个提供者同时注册，进行加索
     */
    private static synchronized void registerService(String serviceName, String host, int port) {
        // 获得当前服务的已有地址集合
        Set<RegisterServiceVo> serviceVoSet = serviceHolder.get(serviceName);
        if (serviceVoSet == null) {
            // 已有地址集合为空，新增集合
            serviceVoSet = new HashSet<>();
            serviceHolder.put(serviceName, serviceVoSet);
        }
        
        // 将新的服务提供者加入集合
        serviceVoSet.add(new RegisterServiceVo(host,port));

        System.out.println("服务已注册[" + serviceName + "]," + "地址[ " + host + "], 端口[" + port + "]" );
    }


    /**
     * 取出服务提供者
     */
    private static Set<RegisterServiceVo> getService(String serviceName) {
        return serviceHolder.get(serviceName);
    }

    /**
     * 处理服务请求的任务，无非就是两种服务
     * 1.服务注册服务
     * 2.服务查询服务
     */
    private static class ServerTask implements Runnable {

        private Socket client = null;
        
        
        public ServerTask(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {
            try (
                    ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
                    ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream())
            ) {
                
                // 检查当前请求是注册服务还是获取服务
                boolean isGetService = inputStream.readBoolean();
                // 服务查询服务,获取服务提供者
                if (isGetService) {
                    String serviceName = inputStream.readUTF();
                    // 取出服务提供者
                    Set<RegisterServiceVo> result = getService(serviceName);
                    // 返回给客户端
                    outputStream.writeObject(result);
                    outputStream.flush();
                    System.out.println("将已注册的服务[" + serviceName + "提供给客户端");
                } else {
                    // 服务注册服务
                    //取得新服务提供方的ip和端口
                    String serviceName = inputStream.readUTF();
                    String host = inputStream.readUTF();
                    int port = inputStream.readInt();
                    // 注册中心保存
                    registerService(serviceName, host, port);
                    outputStream.writeBoolean(true);
                    outputStream.flush();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
//                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 启动注册服务
    public void startService() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        System.out.println("服务注册中心 on :" + port + ": 运行");
        try {
            while (true) {
                new Thread(new ServerTask(serverSocket.accept())).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    @PostConstruct
    public void init() {
        this.port = 9999;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startService();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
