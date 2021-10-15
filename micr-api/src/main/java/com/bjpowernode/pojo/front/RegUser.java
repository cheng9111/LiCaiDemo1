package com.bjpowernode.pojo.front;

import java.io.Serializable;

public class RegUser implements Serializable {
    private String phone;
    private String pwd;
    private String code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "RegUser{" +
                "phone='" + phone + '\'' +
                ", pwd='" + pwd + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
