package com.bjpowernode.service.impl;

import com.bjpowernode.mapper.FinanceAccountMapper;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.service.FinanceAccountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(interfaceClass = FinanceAccountService.class,version = "1.0")
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Resource
    private FinanceAccountMapper accountMapper;

    @Override
    public FinanceAccount queryAccountByUserId(Integer userId) {
        FinanceAccount account = null;
        if(userId != null && userId > 0){
            account = accountMapper.selectAccountByUserId(userId);
        }
        return account;
    }
}
