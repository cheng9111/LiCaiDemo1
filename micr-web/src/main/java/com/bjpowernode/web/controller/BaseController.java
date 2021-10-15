package com.bjpowernode.web.controller;

import com.bjpowernode.service.*;
import org.apache.dubbo.config.annotation.DubboReference;


public class BaseController {
    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    protected UserService userService;

    @DubboReference(interfaceClass = LoanInfoService.class,version = "1.0")
    protected LoanInfoService loanInfoService;

    @DubboReference(version = "1.0")
    protected BidInfoService bidInfoService;

    @DubboReference(version = "1.0")
    protected FinanceAccountService financeAccountService;

    @DubboReference(version = "1.0")
    protected RechargeService rechargeService;


    @DubboReference(version = "1.0")
    protected IncomeService incomeService;

}
