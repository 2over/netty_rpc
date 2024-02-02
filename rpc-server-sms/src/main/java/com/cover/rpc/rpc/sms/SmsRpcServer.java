package com.cover.rpc.rpc.sms;

import com.cover.rpc.remote.SendSms;
import com.cover.rpc.rpc.base.RpcServerFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Random;

@Service
public class SmsRpcServer {
    
    @Autowired
    private RpcServerFrame rpcServerFrame;
    
    @PostConstruct
    public void server() throws IOException {
        Random r = new Random();
        int port = 8778 + r.nextInt(100);
        rpcServerFrame.startService(SendSms.class.getName(), "127.0.0.1", port, SendSmsImpl.class);
    }
}
