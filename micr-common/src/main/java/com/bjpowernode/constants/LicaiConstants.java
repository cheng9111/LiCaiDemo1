package com.bjpowernode.constants;

public class LicaiConstants {

    //产品类型
    //新手宝产品类型
    public static final int PRODUCT_TYPE_XINSHOUBAO=0;
    //优选
    public static final int PRODUCT_TYPE_YOUXUAN=1;
    //散标
    public static final int PRODUCT_TYPE_SANBIAO=2;

    //session中的user
    public static final String SESSION_USER = "licai_sess_user";


    /****************投资记录表的 bid_status ************************/
    //投资成功
    public static final int NORMAL_SUCC=1;

    //未返 0
    public static final Integer INCOME_STATUS_PLAN = 0 ;
    //返还
    public static final Integer INCOME_STATUS_BACK = 1 ;

    /*****************充值表的recharge_status状态****************************/
    //充值中
    public static final Integer RECHARGE_PROCESSING = 0;
    //成功
    public static final Integer RECHARGE_SUCCESS = 1;
    //失败
    public static final Integer RECHARGE_FAILURE = 2;
}
