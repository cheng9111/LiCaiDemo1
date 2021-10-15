package com.bjpowernode.web.controller;

import com.bjpowernode.constants.ErrCode;
import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.constants.LicaiRedisKey;
import com.bjpowernode.model.User;
import com.bjpowernode.pojo.BidLoanInfo;
import com.bjpowernode.pojo.InvestResult;
import com.bjpowernode.pojo.PageInfo;
import com.bjpowernode.pojo.RespObject;
import com.bjpowernode.util.CommonUtil;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
public class BidInfoController extends BaseController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    //投资
    @GetMapping("/loan/invest")
    @ResponseBody
    public RespObject invest(
            @RequestParam("loanId") Integer loanId,
            @RequestParam("bidMoney") Integer bidMoney,
            HttpSession session){



        log.debug("micr-web|bid|invest|begin|"+loanId+"|"+bidMoney);
        RespObject ro  = RespObject.FAIL("请稍后重试");

        if(loanId != null && loanId > 0 && bidMoney != null && ( bidMoney % 100 == 0 ) ){
            User user  = (User) session.getAttribute(LicaiConstants.SESSION_USER);
            log.debug("micr-web|bid|invest|entry|"+loanId+"|"+bidMoney+"|"+user.getId());
            //投资业务处理
            InvestResult investResult = bidInfoService.invest(user.getId(),loanId,new BigDecimal(bidMoney));
            if( investResult.getResult() == 1 ){
                //投资成功
                ro= RespObject.SUCC(investResult.getMessage());
                //更新投资的排行榜
                stringRedisTemplate.opsForZSet().incrementScore(
                        LicaiRedisKey.INVEST_TOP,user.getPhone(),bidMoney);
            } else {
                ro.setMsg( investResult.getMessage()) ;
            }
        } else {
            ro.setCode(ErrCode.ERR_PARAM_EMPTY).setMsg("请求数据不正确");
        }

        return ro;
    }

    //更多投资记录
    @GetMapping("/loan/myInvest")
    public String allInvest(Model model, HttpSession session,
                            Integer pageNo, Integer pageSize){

        User user = (User) session.getAttribute(LicaiConstants.SESSION_USER);

        pageNo = CommonUtil.defaultPageNo(pageNo);
        pageSize = CommonUtil.defaultPageSize(pageSize);

        //分页查询投资记录
        List<BidLoanInfo> bidList = bidInfoService.queryBidListByUid(
                                     user.getId(),pageNo,pageSize);
        model.addAttribute("bidList",bidList);

        //分页的总记录数
        Integer bidRecords = bidInfoService.countBidRecordByUid(user.getId());
        PageInfo pageInfo  = new PageInfo(pageNo,pageSize,bidRecords);

        model.addAttribute("pageInfo",pageInfo);

        //分页查询投资记录
        return "myInvest";
    }

}
