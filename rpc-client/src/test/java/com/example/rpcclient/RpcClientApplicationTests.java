package com.example.rpcclient;

import com.cover.rpc.client.service.NormalBusi;
import com.cover.rpc.remote.SendSms;
import com.cover.rpc.remote.vo.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RpcClientApplicationTests {

    @Test
    void contextLoads() {
    }



    @Autowired
    private NormalBusi normalBusi;

    @Autowired
    private SendSms sendSms;

//    @Test
//    void contextLoads() {
//
//    }

    @Test
    public void rpcTest() {
        long start = System.currentTimeMillis();
        normalBusi.business();

        // 发送邮件
        UserInfo userInfo = new UserInfo("Cover", "181");
        System.out.println("Send mail" + sendSms.sendMail(userInfo));
        System.out.println("共耗时：" + (System.currentTimeMillis() - start));
    }
}
