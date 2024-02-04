package com.cover.rpcnetty.rpc.sms;

import com.cover.rpcnetty.remote.SendSms;
import com.cover.rpcnetty.remote.vo.UserInfo;
import org.springframework.stereotype.Service;

/**
 * 短信息发送服务的实现
 * @author xieh
 * @date 2024/02/04 21:05
 */
@Service
public class SendSmsImpl implements SendSms {

    @Override
    public boolean sendMail(UserInfo userInfo) {
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("以发送短信息给:" + userInfo.getName() + "到【" + userInfo.getPhone());
        return true;
    }
}
