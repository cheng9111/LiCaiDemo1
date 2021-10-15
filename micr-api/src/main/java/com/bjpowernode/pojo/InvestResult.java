package com.bjpowernode.pojo;

import java.io.Serializable;

public class InvestResult implements Serializable {

    private int result;
    private String message;

    public InvestResult() {
    }

    public InvestResult(int result, String message) {
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
