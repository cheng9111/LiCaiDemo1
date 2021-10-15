package com.bjpowernode.service;

import com.bjpowernode.pojo.BidLoanInfo;
import com.bjpowernode.pojo.InvestResult;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoService {

    /**
     * @return 累计投资总金额
     */
    BigDecimal querySumBidMoney();


    /**
     * 根据产品id,查询最近的3条投资记录
     * @param loanId 产品id
     * @return
     */
    List<BidLoanInfo> queryBidListByLoanId(Integer loanId);


    /**
     * @param uid       用户id
     * @param pageNo    页号
     * @param pageSize  页的大小
     * @return  投资记录(产品名称)
     */
    List<BidLoanInfo> queryBidListByUid(Integer uid, Integer pageNo, Integer pageSize);

    /**
     * 某个用户的投资总记录数量
     * @param uid 用户id
     */
    Integer countBidRecordByUid(Integer uid);


    /**
     * @param uid      用户id
     * @param loanId   产品id
     * @param bidMoney 投资金额
     */
    InvestResult invest(Integer uid, Integer loanId, BigDecimal bidMoney);


}
