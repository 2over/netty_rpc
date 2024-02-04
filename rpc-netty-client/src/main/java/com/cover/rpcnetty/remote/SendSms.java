package com.cover.rpcnetty.remote;

import com.cover.rpcnetty.remote.vo.UserInfo;

public interface SendSms {
    
    boolean sendMail(UserInfo userInfo);
}
