package com.cover.rpc.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    
    @Autowired
    private RpcClientFrame rpcClientFrame;
    
}
