package com.bjpowernode.mapper;

import com.bjpowernode.model.FinanceAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface FinanceAccountMapper {

    int insertAccount(FinanceAccount record);

    //查询资金账户
    FinanceAccount selectAccountByUserId(@Param("userId") Integer userId);


    //给行记录上锁
    FinanceAccount selectAccountForUpdate(@Param("userId") Integer userId);

    //投资，扣除资金余额
    int updateDeductAvailableMoney(@Param("uid") Integer uid, @Param("bidMoney") BigDecimal bidMoney);

    //收益，更新金额
    int updateAddAvailableMoneyByIncome(@Param("uid") Integer uid,
                                        @Param("bidMoney") BigDecimal bidMoney,
                                        @Param("incomeMoney") BigDecimal incomeMoney);


    //充值， 更新金额
    int updateAddAvailableMoneyByRecharge(@Param("uid") Integer uid, @Param("money") BigDecimal rechargeMoney);

}