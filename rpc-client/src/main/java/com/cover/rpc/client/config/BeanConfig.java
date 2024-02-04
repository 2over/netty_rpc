package com.cover.rpc.client.config;

import com.cover.rpc.client.rpc.RpcClientFrame;
import com.cover.rpc.remote.SendSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BeanConfig {
    
    @Autowired
    private RpcClientFrame rpcClientFrame;

    @Bean
    public SendSms getSmsService() throws IOException, ClassNotFoundException {
        return rpcClientFrame.getRemoteProxyObject(SendSms.class);
    }

}
