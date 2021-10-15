package com.bjpowernode.controller;

import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.service.KuqiQianService;
import com.bjpowernode.service.RechargeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

@Controller
public class KuaiQianController {


    @Resource
    private KuqiQianService kuqiQianService;



    @RequestMapping("/kq")
    public String receiveMicrWebReq(@RequestParam("uid") Integer uid,
                                    @RequestParam("phone") String phone,
                                    @RequestParam("rechargeMoney") String money,
                                    Model model) {
        try {
            //创建快钱的提交参数
            Map<String, String> data = kuqiQianService.getFormData(uid, phone, money);
            //创建订单记录（db）
            kuqiQianService.createRechargeRecord(uid, money, data);
            //存订单号到redis
            kuqiQianService.addOrderIdToRedis(data);

            model.addAllAttributes(data);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg","请稍后重新充值");
            return "err";
        }
        return "kqForm";
    }


    //接口： 接收快钱的异步通知
    //http://localhost:9002/pay/kq/notify
    //http://47.113.198.114:9999/pay/kq/notify
    //redirecturl:快钱重定向到的商家地址
    @RequestMapping("/kq/notify")
    @ResponseBody
    public String kuaiQianNotify(HttpServletRequest request){

        Enumeration<String> names = request.getParameterNames();
        while(names.hasMoreElements()){
            String name = names.nextElement();
            System.out.println(name+"="+request.getParameter(name));
        }

        try{
            kuqiQianService.handlerNotify(request);
        } finally {
            return "<result>1</result><redirecturl>http://localhost:8000/licai/loan/page/myCenter</redirecturl>";
        }

    }

    //定时任务调用的接口
    @GetMapping("/kq/query")
    @ResponseBody
    public String handlerQuery(){
        //写查询接口的调用
        kuqiQianService.getRedisOrderId();
        return "invoke ok";
    }
}
