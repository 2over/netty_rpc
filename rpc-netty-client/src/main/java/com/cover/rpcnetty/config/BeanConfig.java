package com.cover.rpcnetty.config;

import com.cover.rpcnetty.remote.SendSms;
import com.cover.rpcnetty.rpc.RpcClientFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    private RpcClientFrame rpcClientFrame;
    @Bean
    public SendSms getSmsService() {
        return rpcClientFrame.getRemoteProxyObject(SendSms.class);
    }
}
