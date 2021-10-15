package com.bjpowernode.mapper;

import com.bjpowernode.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UserMapper {

    //注册用户总数
    int selectCountRegisterUsers();

    //查询手机号是否注册
    User selectByPhone(@Param("phone") String phone);

    //添加用户
    int insertUserGetId(User user);

    //实名认证,更新id_card, name字段
    int updateRealName(@Param("phone") String phone,
                       @Param("card") String card,
                       @Param("name") String name);

    //登录
    User selectByPhoneAndPassword(@Param("phone") String phone, @Param("pwd") String pwd);

    //更新登录时间
    int updateLoginTime(@Param("userId") Integer userId, @Param("loginDateTime") Date loginDateTime);

    //chaxun
    User getUser(@Param("phone") String phone);
}