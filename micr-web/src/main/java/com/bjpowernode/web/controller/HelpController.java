package com.bjpowernode.web.controller;

import com.bjpowernode.constants.ErrCode;
import com.bjpowernode.model.User;
import com.bjpowernode.pojo.RespObject;
import com.bjpowernode.util.CommonUtil;
import com.bjpowernode.web.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsSame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class HelpController extends BaseController {

    @Autowired


    @Resource
    private SmsService smsService;

    //判断手机号是否注册过
    @GetMapping("/loan/help/phone")
    @ResponseBody
    public RespObject hasRegisterByPhone(String phone){

        log.debug("****micr-web|查询手机号是否注册|begin|phone|"+phone+"****");
        RespObject ro  = RespObject.FAIL("请求失败,稍后重试");
        //1.检查请求的参数
        if( CommonUtil.checkPhone(phone)){
            //2. 查询手机号
            User user  = userService.queryUserByPhone(phone);
            log.debug("****micr-web|查询手机号是否注册|--|user|"+user+"****");
            if( user == null ){
                ro.setCode(ErrCode.SUCC);
                ro.setMsg("可以注册");
            } else {
                ro.setMsg("请更换手机号");
            }
        } else {
            ro.setCode(ErrCode.ERR_PHONE_FORMAT);
            ro.setMsg("手机号格式不正确");
        }
        log.debug("****micr-web|查询手机号是否注册|end|phone|"+phone+"****");
        return ro;
    }

    //发送短信
    @GetMapping("/loan/help/sendSMS")
    @ResponseBody
    public RespObject sendSms(@RequestParam("phone") String phone){
        RespObject ro  = RespObject.FAIL("请重新获取验证码");
        if( CommonUtil.checkPhone(phone)){
            //发送短信
            boolean isSend = smsService.sendSms(phone);
            if( isSend ){
                ro = RespObject.SUCC("发送短信成功");
            }
        } else {
            ro.setCode(ErrCode.ERR_PHONE_FORMAT).setMsg("手机号格式不正确");
        }
        return ro;
    }
}
