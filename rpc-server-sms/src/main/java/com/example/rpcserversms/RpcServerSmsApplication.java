package com.example.rpcserversms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.cover.rpc.*")
public class RpcServerSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcServerSmsApplication.class, args);
    }

}
