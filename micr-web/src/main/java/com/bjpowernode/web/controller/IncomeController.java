package com.bjpowernode.web.controller;

import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.model.User;
import com.bjpowernode.pojo.PageInfo;
import com.bjpowernode.util.CommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IncomeController extends BaseController{

    //查询更多收益记录
    @GetMapping("/loan/myIncome")
    public String allRecharge(HttpSession session, Model model,
                              Integer pageNo,Integer pageSize){

        User user = (User) session.getAttribute(LicaiConstants.SESSION_USER);
        pageNo = CommonUtil.defaultPageNo(pageNo);
        pageSize = CommonUtil.defaultPageSize(pageSize);

        return "myIncome";

    }
}
