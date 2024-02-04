package com.cover.rpcnetty.remote.vo;

import java.io.Serializable;

/**
 * 用户的实体类，已实现序列化
 * @author xieh
 * @date 2024/02/04 21:02
 */
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

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
