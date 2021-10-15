package com.bjpowernode.service;

import com.bjpowernode.pojo.IncomeLoanInfo;

import java.util.List;

public interface IncomeService {

    //用户的收益记录
    List<IncomeLoanInfo> queryIncomeListByUid(Integer uid, Integer pageNo, Integer pageSize);

    //记录总数
    Integer queryIncomeRecordByUid(Integer uid);

    //生成收益计划
    void generateIncomePlan();

    //收益返还
    void generateIncomeBack();
}
