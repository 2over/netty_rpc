package com.cover.rpc.remote.vo;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = 5744030046311038535L;
    private final String name;
    
    private final String phone;
    
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
