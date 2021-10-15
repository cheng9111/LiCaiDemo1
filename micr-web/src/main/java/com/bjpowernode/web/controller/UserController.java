package com.bjpowernode.web.controller;

import com.bjpowernode.constants.ErrCode;
import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.model.User;
import com.bjpowernode.pojo.BidLoanInfo;
import com.bjpowernode.pojo.IncomeLoanInfo;
import com.bjpowernode.pojo.RespObject;
import com.bjpowernode.pojo.front.RegUser;
import com.bjpowernode.util.CommonUtil;
import com.bjpowernode.web.service.RealNameService;
import com.bjpowernode.web.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
public class UserController extends BaseController {

    @Resource
    private SmsService smsService;

    @Resource
    private RealNameService realNameService;

    //注册页面
    @GetMapping("/loan/page/register")
    public String pageRegister(Model model){
        return "register";
    }
    //实名认证
    @GetMapping("/loan/page/realName")
    public String pageRealName(){
        return "realName";
    }

    //用户中心
    @GetMapping("/loan/page/myCenter")
    public String pageMyCenter(Model model,HttpSession session){

        //查询资金
        User user  = (User) session.getAttribute(LicaiConstants.SESSION_USER);
        FinanceAccount account = financeAccountService.queryAccountByUserId(user.getId());
        if( account != null){
            model.addAttribute("accountMoney",account.getAvailableMoney());
        }

        //查询投资记录
        List<BidLoanInfo> bidList = bidInfoService.queryBidListByUid(user.getId(),1,5);
        Integer bidRecords = bidInfoService.countBidRecordByUid(user.getId());
        model.addAttribute("bidRecords",bidRecords);
        model.addAttribute("bidList",bidList);

        //查询最近的5条充值记录
        List<RechargeRecord> rechargeList = rechargeService.
                                     queryRechargeListByUid(user.getId(),1,5);
        model.addAttribute("rechargeList",rechargeList);

        //查询最近的5条收益记录
        List<IncomeLoanInfo> incomeList = incomeService.queryIncomeListByUid(user.getId(),1,5);
        Integer incomeRecords = incomeService.queryIncomeRecordByUid(user.getId());
        model.addAttribute("incomeList",incomeList);
        model.addAttribute("incomeRecords",incomeRecords);

        return "myCenter";
    }

    //登录
    @GetMapping("/loan/page/login")
    public String pageLogin(String returnUrl, HttpServletRequest request){
        //首页地址
        String url = request.getScheme() + "://" + request.getServerName()
                +   ":" + request.getServerPort()
                +   request.getContextPath() + "/index";
        returnUrl = StringUtils.isEmpty(returnUrl) ? url : returnUrl;

        //注册用户数量
        Integer registerUsers = userService.queryRegisterUsers();
        //累计成交金额
        BigDecimal sumBidMoney = bidInfoService.querySumBidMoney();

        request.setAttribute("registerUsers",registerUsers);
        request.setAttribute("sumBidMoney",sumBidMoney);
        request.setAttribute("returnUrl",returnUrl);

        return "login";
    }

    //查询金额
    @GetMapping("/user/account")
    @ResponseBody
    public RespObject queryAccount(HttpSession session){
        log.debug("micr-web|queryAccount|begin");
        RespObject ro  = RespObject.FAIL("查询失败");

        User user = (User) session.getAttribute(LicaiConstants.SESSION_USER);
        FinanceAccount account = financeAccountService.queryAccountByUserId(user.getId());
        if( account != null){
            log.debug("micr-web|queryAccount|account|"+account.getId());
            ro = new RespObject(ErrCode.SUCC,"查询成功",account.getAvailableMoney());
            log.info("micr-web|queryAccount|account|"+account.getId()
                    +"|userid|"+user.getId()+"|"+account.getAvailableMoney());
        } else {
            log.debug("micr-web|queryAccount|account|notexits");
            ro.setCode(ErrCode.ERR_ACCOUNT_NOTEXITS);
            ro.setMsg("资金用户不存在");
        }

        log.debug("micr-web|queryAccount|end");
        return ro;
    }

    //登录功能
    @PostMapping("/user/login")
    @ResponseBody
    public RespObject login(@RequestParam("phone") String phone,
                            @RequestParam("pwd") String pwd,
                            @RequestParam("code") String code,
                            HttpSession session){
        RespObject ro  = RespObject.FAIL("登录失败");
        //1.检查数据
        if(!CommonUtil.checkPhone(phone)){
           ro.setCode(ErrCode.ERR_PHONE_FORMAT).setMsg("手机号格式不正确");
        } else if( StringUtils.isEmpty(pwd) || StringUtils.isEmpty(code)){
            ro.setCode(ErrCode.ERR_PARAM_EMPTY).setMsg("请求数据有误");
        } else {
            //请求参数正确
            //2.调用service处理业务逻辑
            User user  = userService.login(phone,pwd);
            if( user != null){
                //3.根据service处理结果, 指定视图,显示不同的数据
                ro  = RespObject.SUCC("登录成功");
                //把user存到session
                session.setAttribute(LicaiConstants.SESSION_USER,user);
            }
        }
        return ro;
    }

    //退出
    @GetMapping("/user/logout")
    public String logout(HttpSession session){
        //1.让session无效
        session.invalidate();
        //2.回到首页面
        return "redirect:/index";
    }

    //注册用户
    @PostMapping("/loan/register")
    @ResponseBody
    public RespObject register(RegUser user, HttpSession session){
        RespObject ro = RespObject.FAIL("注册失败");
        if(StringUtils.isEmpty(user.getPhone()) || !CommonUtil.checkPhone(user.getPhone())){
            ro.setCode(ErrCode.ERR_PHONE_FORMAT);
            ro.setMsg("手机号格式不正确");
        } else if( StringUtils.isEmpty(user.getPwd()) || user.getPwd().length() !=32){
            ro.setCode(ErrCode.ERR_PASSWORD_FORMAT);
            ro.setMsg("必须输入密码");
        } else if(!smsService.checkCode(user.getPhone(),user.getCode())) { //判断短信验证码
            ro.setCode(ErrCode.ERR_SMS_CODE_FORMAT);
            ro.setMsg("短信验证码错误");
        } else {
            //数据正确, 注册用户
            //注册成功的User对象
            User regUser = userService.userRegister(user);
            if("register".equals(regUser.getAction())){
                //注册成功
                ro.setCode(ErrCode.SUCC);
                ro.setMsg("注册成功");

                //把用户存放到session
                session.setAttribute(LicaiConstants.SESSION_USER, regUser);
            } else {
                ro.setCode(ErrCode.ERR_PHONE_HAS_REGISTER);
                ro.setMsg("手机号已经注册过");
            }
        }
        return ro;
    }


    //实名认证
    @PostMapping("/loan/realName")
    @ResponseBody
    public RespObject realName(@RequestParam("phone") String phone,
                               @RequestParam("name") String name,
                               @RequestParam("card") String card,
                               HttpSession session){
        RespObject ro  = RespObject.FAIL("请稍后重新认证");
        if( !CommonUtil.checkPhone(phone)){
            ro.setCode(ErrCode.ERR_PHONE_FORMAT).setMsg("手机号格式不正确");
        } else if( StringUtils.isEmpty(name) || StringUtils.isEmpty(card)){
            ro.setCode(ErrCode.ERR_PARAM_EMPTY).setMsg("请求参数为空");
        } else if( name.length() < 2 || card.length() < 15){
            ro.setCode(ErrCode.ERR_PARAM_EMPTY).setMsg("请求参数为空");
        } else {
            //数据正确 ,请求中手机号和session中手机号一样
            User user = (User) session.getAttribute(LicaiConstants.SESSION_USER);
            if( phone.equals(user.getPhone())){
                //调用认证接口
                boolean result = realNameService.realName(phone,card,name);
                if( result ){
                    //更新数据库
                    if( userService.handlerRealName(phone,card,name)){
                        ro.setCode(ErrCode.SUCC).setMsg("认证成功");
                    }
                } else {
                    ro.setCode(ErrCode.ERR_REALNAME_FAIL).setMsg("认证失败");
                }
            } else{
                ro.setCode(ErrCode.ERR_PHONE_REALNAME).setMsg("认证手机号不一致");
            }
        }
        return ro;

    }
}
