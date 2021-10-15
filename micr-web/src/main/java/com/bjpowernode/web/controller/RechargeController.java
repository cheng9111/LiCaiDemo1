package com.bjpowernode.web.controller;

import com.bjpowernode.config.PayUrl;
import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.model.User;
import com.bjpowernode.pojo.PageInfo;
import com.bjpowernode.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RechargeController  extends BaseController{

    @Resource
    private PayUrl payUrl;
    //查询更多充值记录
    @GetMapping("/loan/myRecharge")
    public String allRecharge(HttpSession session, Model model,
                              Integer pageNo,Integer pageSize){

        User user = (User) session.getAttribute(LicaiConstants.SESSION_USER);

        pageNo = CommonUtil.defaultPageNo(pageNo);
        pageSize = CommonUtil.defaultPageSize(pageSize);

        List<RechargeRecord> rechargeList = rechargeService
                           .queryRechargeListByUid(user.getId(),pageNo,pageSize);
        Integer records = rechargeService.queryRechargeRecordsByUid(user.getId());
        PageInfo pageInfo = new PageInfo(pageNo,pageSize, records);

        model.addAttribute("rechargeList",rechargeList);
        model.addAttribute("pageInfo",pageInfo);

        return "myRecharge";

    }

    @GetMapping("/loan/page/toRecharge")
    public String pageRecharge(Model model){
        System.out.println("payurl="+payUrl.toString());
        model.addAttribute("payurl",payUrl);
        return "toRecharge";
    }
}
