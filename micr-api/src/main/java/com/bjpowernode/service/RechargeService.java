package com.bjpowernode.service;

import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.pojo.RechargeResult;

import java.util.List;

public interface RechargeService {

    //查询某个用户充值记录
    List<RechargeRecord> queryRechargeListByUid(Integer uid,
                                                Integer pageNo,
                                                Integer pageSize);

    //用户充值记录总数
    Integer queryRechargeRecordsByUid(Integer uid);

    //创建充值记录
    int addRechargeRecored(RechargeRecord rechargeRecord);

    //处理异步通知
    RechargeResult handlerRechargeNotify(String orderId, String payResult, String payAmount,String channel);

    //创建
    /*int updateAdd();*/
}
