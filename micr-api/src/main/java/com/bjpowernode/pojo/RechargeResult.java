package com.bjpowernode.pojo;

import java.io.Serializable;

public class RechargeResult implements Serializable {

    private int result;
    private String message;

    public RechargeResult() {
    }

    public RechargeResult(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public void setResult(int result, String message){
        this.result = result;
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
