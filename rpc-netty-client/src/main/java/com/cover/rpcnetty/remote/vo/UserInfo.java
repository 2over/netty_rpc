package com.cover.rpcnetty.remote.vo;

import java.io.Serializable;

public class UserInfo implements Serializable {
    
    private String name;
    
    private String phone;
    
    public UserInfo() {
        
    }
    
    public UserInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
