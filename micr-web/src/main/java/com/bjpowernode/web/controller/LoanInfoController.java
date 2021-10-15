package com.bjpowernode.web.controller;

import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.constants.LicaiRedisKey;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.model.User;
import com.bjpowernode.pojo.BidLoanInfo;
import com.bjpowernode.pojo.InvestTopBean;
import com.bjpowernode.pojo.PageInfo;
import com.bjpowernode.service.LoanInfoService;
import com.bjpowernode.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class LoanInfoController  extends BaseController{

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    //分页查询产品
    @GetMapping("/loan/loan")
    public String loan(@RequestParam("ptype") Integer productType,
                       @RequestParam(value = "pageNo",required = false, defaultValue = "1") Integer pageNo,
                       @RequestParam(value = "pageSize",required = false,defaultValue = "9") Integer pageSize,
                       Model model){

        log.debug("Dubbo消费者Micr-Web LoanInfoController, /loan/loan:"+productType+"|"+pageNo+"|"+pageSize);
        PageInfo pageInfo = new PageInfo();
        List<LoanInfo> loanInfoList = new ArrayList<>();
        if(CommonUtil.checkProductType(productType)){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            loanInfoList = loanInfoService.queryLoanInfoByTypePage(productType,pageNo,pageSize);
            //获取记录总数
            int records = loanInfoService.queryRecordsByProductType(productType);
            pageInfo = new PageInfo(pageNo,pageSize,records);

            //获取投资排行榜
            Set<ZSetOperations.TypedTuple<String>> sets = stringRedisTemplate.opsForZSet()
                    .reverseRangeWithScores( LicaiRedisKey.INVEST_TOP, 0, 4);

            List<InvestTopBean> topList = new ArrayList<>();
            //遍历set
            sets.forEach( c->{
                topList.add(new InvestTopBean(c.getValue(),c.getScore()));
            });
            model.addAttribute("topList",topList);


        }
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("loanInfoList",loanInfoList);
        return "loan";
    }

    //产品详情页面
    @GetMapping("/loan/loanInfo")
    public String loanInfo(@RequestParam("id") Integer id, Model model,
                           HttpSession session){

        LoanInfo loanInfo = null;
        List<BidLoanInfo> bidLoanInfoList = new ArrayList<>();
        if( id != null && id > 0 ){
            //调用dataservice查询产品
            loanInfo = loanInfoService.queryById(id);
            if( loanInfo == null){
                model.addAttribute("msg","产品不存在");
                return "err";
            }
            //查询投资记录
            bidLoanInfoList = bidInfoService.queryBidListByLoanId(id);

            //用户的资金
            User user = (User) session.getAttribute(LicaiConstants.SESSION_USER);
            if( user != null){
               FinanceAccount account= financeAccountService.queryAccountByUserId(user.getId());
               if( account != null){
                   model.addAttribute("accountMoney",account.getAvailableMoney());
               }
            }
        }

        model.addAttribute("loanInfo",loanInfo);
        model.addAttribute("bidList",bidLoanInfoList);
        return "loanInfo";
    }
}
