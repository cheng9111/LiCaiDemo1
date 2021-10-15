package com.bjpowernode.service;

import com.bjpowernode.model.User;
import com.bjpowernode.pojo.front.RegUser;

public interface UserService {

    /**
     * 注册用户的总数
     */
    Integer queryRegisterUsers();

    /**
     * @param phone 手机号
     * @return user对象或null
     */
    User queryUserByPhone(String phone);

    /**
     * @param user 参数
     * @return 注册成功的用户
     */
    User userRegister(RegUser user);

    /**
     * 处理数据库实名认证
     * @param phone 手机号
     * @param card  身份证号
     * @param name  姓名
     * @return 是否更新数据成功
     */
    boolean handlerRealName(String phone, String card, String name);


    /**
     * 登录
     * @param phone 手机号
     * @param pwd   密码
     * @return 登录的user或者null
     */
    User login(String phone, String pwd);
}
