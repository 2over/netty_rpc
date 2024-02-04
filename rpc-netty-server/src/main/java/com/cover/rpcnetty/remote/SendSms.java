package com.cover.rpcnetty.remote;

import com.cover.rpcnetty.remote.vo.UserInfo;

/**
 * 短信息发送接口
 * @author xieh
 * @date 2024/02/04 21:01
 */

public interface SendSms {

    // 发送短信
    boolean sendMail(UserInfo userInfo);
}
