package com.cover.rpcnetty.rpc.base.vo;

public enum MessageType {
    
    // 业务请求消息
    SERVICE_REQ((byte) 0),
    
    // 业务应答消息
    SERVICE_RESP((byte) 1),
    
    // 无需应答的业务请求消息
    ONE_WAY((byte) 2),
    
    // 登录请求消息
    LOGIN_REQ((byte) 3),
    // 登录响应消息
    LOGIN_RESP((byte) 4),
    
    //心跳请求消息
    HEARTBEAT_REQ((byte) 5),
    
    // 心跳应答消息
    HEARTBEAT_RESP((byte) 6)
    ;
    
    private byte value;
    
    private MessageType(byte value) {
        this.value = value;
    }
    
    public byte value() {
        return this.value;
    }
}
