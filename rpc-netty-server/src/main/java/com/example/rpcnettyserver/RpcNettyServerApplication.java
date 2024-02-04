package com.example.rpcnettyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.cover.rpcnetty.*")
public class RpcNettyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcNettyServerApplication.class, args);
    }

}
