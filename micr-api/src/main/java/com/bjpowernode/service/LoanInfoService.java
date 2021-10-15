package com.bjpowernode.service;

import com.bjpowernode.model.LoanInfo;

import java.math.BigDecimal;
import java.util.List;

public interface LoanInfoService {

    /**
     * @return 历史年化收益率平均值
     */
    BigDecimal queryHistoryAvgRate();


    /**
     * @param type      产品类型
     * @param pageNo    页号
     * @param pageSize  每页大小
     * @return  产品List
     */
    List<LoanInfo> queryLoanInfoByTypePage(Integer type, Integer pageNo, Integer pageSize);

    /***
     * 总记录数量
     * @param type  产品类型
     */
    int queryRecordsByProductType(Integer type);

    /**
     * @param id 产品id
     * @return 查询的产品对象( null, 产品对象)
     */
    LoanInfo queryById(Integer id);
}
