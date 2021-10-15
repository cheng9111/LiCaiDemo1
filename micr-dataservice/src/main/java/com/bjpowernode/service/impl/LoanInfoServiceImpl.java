package com.bjpowernode.service.impl;

import com.bjpowernode.mapper.LoanInfoMapper;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.service.LoanInfoService;
import com.bjpowernode.util.CommonUtil;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = LoanInfoService.class,version = "1.0")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Override
    public BigDecimal queryHistoryAvgRate() {
        BigDecimal historyAvgRate = loanInfoMapper.selectHistoryAvgRate();
        return historyAvgRate;
    }

    /**
     * @param type      产品类型
     * @param pageNo    页号
     * @param pageSize  每页大小
     * @return  产品List
     */
    @Override
    public List<LoanInfo> queryLoanInfoByTypePage(Integer type,
                                                  Integer pageNo,
                                                  Integer pageSize) {
        //参数检查 1. 产品类型有效(0,1,2) , 2. pageN0 >=1 ,  3 .pageSize 5---100
        List<LoanInfo>  loanInfoList = new ArrayList<>();
        if(CommonUtil.checkProductType(type) ){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            int offSet = (pageNo - 1 ) * pageSize;
            loanInfoList = loanInfoMapper.selectLoanInfoByTypePage(type,offSet , pageSize);
        }
        return loanInfoList;
    }

    /***
     * 总记录数量
     * @param type  产品类型
     */
    @Override
    public int queryRecordsByProductType(Integer type) {
        int records = 0;
        if(CommonUtil.checkProductType(type) ){
            records = loanInfoMapper.selectRecordsByProductType(type);
        }
        return records;
    }

    /**
     * @param id 产品id
     * @return 查询的产品对象( null, 产品对象)
     */
    @Override
    public LoanInfo queryById(Integer id) {
        LoanInfo loanInfo = null;
        if( id != null && id > 0 ){
            loanInfo  = loanInfoMapper.selectById(id);
        }
        return loanInfo;
    }
}
