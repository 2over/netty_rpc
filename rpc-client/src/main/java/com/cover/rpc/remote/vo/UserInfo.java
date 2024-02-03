package com.cover.rpc.remote.vo;

import java.io.Serializable;

/**
 * @author xieh
 * @date 2024/02/03 17:48
 */
public class UserInfo implements Serializable {

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
