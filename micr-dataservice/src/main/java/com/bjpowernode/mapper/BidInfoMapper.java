package com.bjpowernode.mapper;

import com.bjpowernode.model.BidInfo;
import com.bjpowernode.pojo.BidLoanInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoMapper {

    //投资的金额和
    BigDecimal selectSumBidMoney();

    //某个产品的投资记录
    List<BidLoanInfo> selectBidList(@Param("loanId") Integer loanId);

    //某个用户投资记录
    List<BidLoanInfo> selectBidListByUid(@Param("uid") Integer uid,
                                         @Param("offSet") Integer offSet,
                                         @Param("rows") Integer rows);

    //查询产品的所有投资记录
    List<BidInfo> selectListByLoanId(@Param("loanId") Integer loanId);

    //某个用户投资总记录
    Integer selectCountBidByUid(@Param("uid") Integer uid);

    //添加投资记录
    int insertBidInfo(BidInfo bidInfo);
}