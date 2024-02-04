package com.example.rpcnettyclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.cover.rpcnetty.*")
public class RpcNettyClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcNettyClientApplication.class, args);
    }

}
