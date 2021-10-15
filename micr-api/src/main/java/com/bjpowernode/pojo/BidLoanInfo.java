package com.bjpowernode.pojo;

import com.bjpowernode.model.BidInfo;

public class BidLoanInfo extends BidInfo {
    private String phone;
    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
