package com.cover.rpc.rpc.base;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 注册服务，引入了服务的注册和发现机制
@Service
public class RegisterServiceWithRegCenter {
    
    // 本地提供服务的一个名单，用缓存实现
    private static final Map<String, Class> serviceCache = new ConcurrentHashMap<>();
    
    // 往远程注册服务器注册本服务，同时在本地注册本服务
    public void regRemote(String serviceName, String host, int port, Class impl) throws IOException {
        // 登记到注册中心
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 9999));
            
            output = new ObjectOutputStream(socket.getOutputStream());
            // 注册服务
            output.writeBoolean(false);
            // 提供的服务名
            output.writeUTF(serviceName);
            // 服务提供方的IP
            output.writeUTF(host);
            // 服务提供方的端口
            output.writeInt(port);
            
            output.flush();
            
            input = new ObjectInputStream(socket.getInputStream());
            if (input.readBoolean()) {
                System.out.println("服务[" + serviceName + "]注册成功!");
            }
            
            // 可提供服务放入本地缓存
            serviceCache.put(serviceName, impl);
        } catch (Exception e) {
            e.printStackTrace();
            if (socket != null) {
                socket.close();
            }
            
            if (output != null) {
                output.close();
            }
            
            if (input != null) {
                input.close();
            }
        }
        
    }
    
    
    // 获取服务
    public Class getLocalService(String serviceName) {
        return serviceCache.get(serviceName);
    }
}
