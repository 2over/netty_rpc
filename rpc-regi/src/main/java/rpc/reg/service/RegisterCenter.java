package rpc.reg.service;

import remote.vo.RegisterServiceVo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// 服务注册中心，服务提供者在启动时需要注册中心登记自己的信息
public class RegisterCenter {
    // key表示服务名，value代表
    private static final Map<String, Set<RegisterServiceVo>> serviceHolder 
            = new HashMap<>();
    // 注册服务端口号
    private int port;
    
    private static synchronized void registerService(String serviceName, String host, int port) {
        // 获得当前服务的已有地址集合
        Set<RegisterServiceVo> serviceVOSet = serviceHolder.get(serviceName);

    }
    
    
}
