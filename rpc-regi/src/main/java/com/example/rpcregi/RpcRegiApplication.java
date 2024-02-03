package com.example.rpcregi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.cover.rpc.rpc.reg.service")
public class RpcRegiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcRegiApplication.class, args);
    }

}
