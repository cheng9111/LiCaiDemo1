package com.bjpowernode.service;

import com.bjpowernode.model.FinanceAccount;

//资金服务
public interface FinanceAccountService {

    FinanceAccount queryAccountByUserId(Integer userId);
}
