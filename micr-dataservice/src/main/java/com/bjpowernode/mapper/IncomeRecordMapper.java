package com.bjpowernode.mapper;

import com.bjpowernode.model.IncomeRecord;
import com.bjpowernode.pojo.IncomeLoanInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IncomeRecordMapper {


    //用户的收益记录
    List<IncomeLoanInfo> selectIncomeListByUid(@Param("uid") Integer uid,
                                               @Param("offSet") Integer offSet,
                                               @Param("rows") Integer rows);
    //用户的总收益记录数量
    Integer selectCountRecordByUid(@Param("uid") Integer uid);

    //到期的收益记录
    List<IncomeRecord> selectExpiredIncome();

    //添加记录
    int insertIncomeRecord(IncomeRecord ir);


    //更新收益的状态
    int updateStatusBack(Integer uid);
}