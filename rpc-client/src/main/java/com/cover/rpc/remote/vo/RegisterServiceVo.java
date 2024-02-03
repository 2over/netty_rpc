package com.cover.rpc.remote.vo;

import java.io.Serializable;

// 注册中心注册服务的实体类
public class RegisterServiceVo implements Serializable {

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
