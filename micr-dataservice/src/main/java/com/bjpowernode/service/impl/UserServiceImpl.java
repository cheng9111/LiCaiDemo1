package com.bjpowernode.service.impl;

import com.bjpowernode.constants.LicaiRedisKey;
import com.bjpowernode.mapper.FinanceAccountMapper;
import com.bjpowernode.mapper.UserMapper;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.User;
import com.bjpowernode.pojo.front.RegUser;
import com.bjpowernode.service.UserService;
import com.bjpowernode.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
//服务提供者
@DubboService(interfaceClass = UserService.class,version = "1.0")
public class UserServiceImpl implements UserService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public  Integer queryRegisterUsers() {
        log.debug("====Dubbo-UserService服务,查询注册用户数开始====");
        //1.从redis获取数据.
        Integer registUsers = (Integer)redisTemplate.opsForValue()
                              .get(LicaiRedisKey.PLAT_REGISTER_USERS);
        log.debug("====Dubbo-UserService服务,查询注册用户数,redis数据="+registUsers);
        //2.判断数据是否有
        if( registUsers == null){
            synchronized (this){
                //从新redis取数据
                registUsers = (Integer)redisTemplate.opsForValue()
                                      .get(LicaiRedisKey.PLAT_REGISTER_USERS);
                if( registUsers == null){
                    //3.从数据库重新获取数据
                    registUsers = userMapper.selectCountRegisterUsers();
                    log.debug("====Dubbo-UserService服务,查询注册用户数,DB数据="+registUsers);
                    //4.把数据放到redis , 数据有效时间 30 分钟
                    redisTemplate.opsForValue().set(LicaiRedisKey.PLAT_REGISTER_USERS,registUsers,30, TimeUnit.MINUTES);
                }
            }
        }

        log.debug("****Dubbo-UserService服务,查询注册用户数完成****");
        log.info("Dubbo-UserService服务,查询注册用户数="+registUsers);
        //5.返回数据
        return registUsers;
    }

    /**
     * @param phone 手机号
     * @return user对象或null
     */
    @Override
    public User queryUserByPhone(String phone) {
        User user = null;
        if(CommonUtil.checkPhone(phone)){
            user  = userMapper.selectByPhone(phone);
        }
        return user;
    }

    /**
     * @param puser 参数
     * @return 注册成功的用户
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User userRegister(RegUser puser) {
        log.debug("micr-dataservice注册用户-"+puser.toString());

        User user = userMapper.selectByPhone(puser.getPhone());
        log.debug("micr-dataservice注册用户-查询手机号phone="+puser.getPhone()+"|user="+user);

        if( user == null ){
            //1.添加到 u_user
            user = new User();
            user.setAction("register");
            user.setPhone(puser.getPhone());
            user.setLoginPassword(puser.getPwd());
            userMapper.insertUserGetId(user);

            //2.添加到 u_finance_account
            FinanceAccount account = new FinanceAccount();
            account.setUid(user.getId());
            account.setAvailableMoney(new BigDecimal("888"));
            financeAccountMapper.insertAccount(account);
            log.debug("micr-dataservice注册用户-注册手机号phone="+puser.getPhone());
            //3.发送站内信, 邮件
            log.info("micr-dataservice注册用户-注册手机号phone="+puser.getPhone());
        } else {
            user.setAction("dbexists");
        }
        return user;
    }

    /**
     * 处理数据库实名认证
     * @param phone 手机号
     * @param card  身份证号
     * @param name  姓名
     * @return 是否更新数据成功
     */
    @Override
    public boolean handlerRealName(String phone, String card, String name) {

        boolean result =false;
        if( CommonUtil.checkPhone(phone) && card != null && name != null){
            //更新数据库
            int rows = userMapper.updateRealName(phone,card,name);
            result = rows > 0 ? true :false;
        }
        return result;
    }

    /**
     * 登录
     * @param phone 手机号
     * @param pwd   密码
     * @return 登录的user或者null
     */
    @Override
    public User login(String phone, String pwd) {
        User  user  = null;
        if( CommonUtil.checkPhone(phone) && pwd != null && pwd.length() == 32){
            user = userMapper.selectByPhoneAndPassword(phone,pwd);
            if( user != null){
                userMapper.updateLoginTime(user.getId(), new Date());
            }
        }
        return user;
    }
}
