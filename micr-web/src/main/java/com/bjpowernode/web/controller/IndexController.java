package com.bjpowernode.web.controller;

import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.service.BidInfoService;
import com.bjpowernode.service.LoanInfoService;
import com.bjpowernode.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
public class IndexController extends BaseController {

    //进入到首页
    @GetMapping({"/index","/"})
    public String toIndex(Model model){

        log.debug("===Dubbo消费者Micr-Web,IndexController /index 开始====");
        //注册用户数量
        Integer registerUsers = userService.queryRegisterUsers();
        model.addAttribute("registerUsers",registerUsers);

        //收益率平均值
        BigDecimal historyAvgRate = loanInfoService.queryHistoryAvgRate();
        model.addAttribute("historyAvgRate",historyAvgRate);

        //累计投资金额
        BigDecimal sumBidMoney  = bidInfoService.querySumBidMoney();
        model.addAttribute("sumBidMoney",sumBidMoney);

        //获取新手宝的产品
        List<LoanInfo> xinShouLoanInfoList =
                loanInfoService.queryLoanInfoByTypePage(LicaiConstants.PRODUCT_TYPE_XINSHOUBAO,1,1);
        model.addAttribute("xinLoanInfoList",xinShouLoanInfoList);

        //优选产品
        List<LoanInfo> youXuanLoanInfoList =
                loanInfoService.queryLoanInfoByTypePage(LicaiConstants.PRODUCT_TYPE_YOUXUAN,1,4);
        model.addAttribute("youXuanInfoList",youXuanLoanInfoList);

        //散标
        List<LoanInfo> sanBiaoLoanInfoList =
                loanInfoService.queryLoanInfoByTypePage(LicaiConstants.PRODUCT_TYPE_SANBIAO,1,8);
        model.addAttribute("sanBiaoInfoList",sanBiaoLoanInfoList);

        log.info("===Dubbo消费者Micr-Web,IndexController /index,查询结果数据:"
                +"registerUsers="+registerUsers
                +";historyAvgRate="+historyAvgRate
                +";sumBidMoney="+sumBidMoney
                +";新手宝记录数量:"+xinShouLoanInfoList.size()
                +";优选记录数量:"+youXuanLoanInfoList.size()
                +";散标记录数量:"+sanBiaoLoanInfoList.size()
        );
        log.debug("===Dubbo消费者Micr-Web,IndexController /index 完成====");
        return "index";//逻辑名称
    }
}
