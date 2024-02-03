package com.cover.rpc.remote;

import com.cover.rpc.remote.vo.UserInfo;

/**
 * @author xieh
 * @date 2024/02/03 17:47
 */
// 短信发送接口
public interface SendSms {
    boolean sendMail(UserInfo userInfo);
}
