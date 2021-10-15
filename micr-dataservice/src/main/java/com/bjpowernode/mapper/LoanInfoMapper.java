package com.bjpowernode.mapper;

import com.bjpowernode.model.LoanInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface LoanInfoMapper {
    //查询历史年化收益率
    BigDecimal selectHistoryAvgRate();

    /**
     * 按产品类型查询产品, 并且分页
     * @param type   产品类型
     * @param offSet limit起始位置
     * @param rows   limit的行数
     * @return 多个产品的集合List
     */
    List<LoanInfo> selectLoanInfoByTypePage(@Param("type") int type,
                                            @Param("offSet") int offSet,
                                            @Param("rows") int rows);
    /***
     * 总记录数量
     * @param type  产品类型
     */
    int selectRecordsByProductType(@Param("type") Integer type);

    /**
     * @param id 产品id
     * @return 查询的产品对象( null, 产品对象)
     */
    LoanInfo selectById(@Param("id") Integer id);

    //扣除商品剩余可投资金额
    int updateDeductLeftMoney(@Param("id") Integer id, @Param("bidMoney") BigDecimal bidMoney);

    //更新产品为满标状态
    int updateStatusAndFullTime(@Param("loanId") Integer loanId);

    //查询满标的没有生成收益计划的产品列表
    List<LoanInfo> selectManBiaoList(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    //更新产品的状态为2
    int updateStatusGeneratePlan(@Param("id") Integer id);
}