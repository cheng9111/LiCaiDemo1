package com.bjpowernode.pojo;

public class InvestTopBean {

    private String phone;
    private Double money;

    public InvestTopBean() {
    }

    public InvestTopBean(String phone, Double money) {
        this.phone = phone;
        this.money = money;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
}
