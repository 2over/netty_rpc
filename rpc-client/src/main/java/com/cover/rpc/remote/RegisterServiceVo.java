package com.cover.rpc.remote;

import java.io.Serializable;

// 注册中心注册服务的实体类
public class RegisterServiceVo implements Serializable {
    private static final long serialVersionUID = -1837591451136962639L;
    
    // 服务提供者的ip地址
    private final String host; 
    
    // 服务提供者端口
    private final int port;
    
    public RegisterServiceVo (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
