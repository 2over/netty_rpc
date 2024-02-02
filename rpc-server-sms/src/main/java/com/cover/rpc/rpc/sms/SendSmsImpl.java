package com.cover.rpc.rpc.sms;

import com.cover.rpc.remote.SendSms;
import com.cover.rpc.remote.vo.UserInfo;

public class SendSmsImpl implements SendSms {
    @Override
    public boolean sendMail(UserInfo userInfo) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("已发送短信息给 :" + userInfo.getName() + "到[" + userInfo.getPhone());
        return true;
    }
}
