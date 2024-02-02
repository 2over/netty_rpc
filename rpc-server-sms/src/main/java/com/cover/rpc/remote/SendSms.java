package com.cover.rpc.remote;


import com.cover.rpc.remote.vo.UserInfo;

// 短信息发送接口
public interface SendSms {
    
    boolean sendMail(UserInfo userInfo);
}
