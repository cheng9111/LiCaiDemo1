package com.bjpowernode.service.impl;

import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.mapper.BidInfoMapper;
import com.bjpowernode.mapper.FinanceAccountMapper;
import com.bjpowernode.mapper.LoanInfoMapper;
import com.bjpowernode.model.BidInfo;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.pojo.BidLoanInfo;
import com.bjpowernode.pojo.InvestResult;
import com.bjpowernode.service.BidInfoService;
import com.bjpowernode.util.CommonUtil;
import com.bjpowernode.util.DecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@DubboService(version = "1.0")
public class BidInfoServiceImpl implements BidInfoService {

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private LoanInfoMapper loanInfoMapper;

    /**
     * @param uid      用户id
     * @param loanId   产品id
     * @param bidMoney 投资金额
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvestResult invest(Integer uid, Integer loanId, BigDecimal bidMoney) {

        log.debug("micr-dataservice|invest|begin|"+uid+"|"+loanId+"|"+bidMoney);
        InvestResult result  = new InvestResult(0,"未知问题");
        //1.用户上锁，校验用户资金
        FinanceAccount financeAccount = financeAccountMapper.selectAccountForUpdate(uid);
        if( financeAccount != null ) {
            if(DecimalUtil.ge(financeAccount.getAvailableMoney(),bidMoney) ){
                //2.获取产品信息
                LoanInfo loanInfo = loanInfoMapper.selectById(loanId);
                if( loanInfo != null ){
                    //获取产品的类型， 剩余金额， min， max
                    //3.校验bidMoney和产品金额
                    if( DecimalUtil.ge(loanInfo.getLeftProductMoney(),bidMoney)
                            && DecimalUtil.ge(loanInfo.getBidMaxLimit(),bidMoney)
                            && DecimalUtil.ge(bidMoney,loanInfo.getBidMinLimit()) ) {
                        //4.扣除用户的资金金额
                        int rows =  financeAccountMapper.updateDeductAvailableMoney(uid,bidMoney);
                        if( rows < 1){
                            log.debug("micr-dataservice|invest|update|availableMoney|"+uid+"|更新0行记录");
                            throw new RuntimeException("用户购买理财产品，扣除账户资金失败");
                        }
                        //5.扣除产品的剩余可投资金额
                        rows = loanInfoMapper.updateDeductLeftMoney(loanId,bidMoney);
                        if( rows < 1){
                            log.debug("micr-dataservice|invest|update|leftMoney|"+uid+"|更新0行记录");
                            throw new RuntimeException("用户购买理财产品，扣除产品剩余可投资金额失败");
                        }
                        //6.生成投资记录
                        BidInfo bidInfo = new BidInfo();
                        bidInfo.setBidMoney(bidMoney);
                        bidInfo.setBidStatus(LicaiConstants.NORMAL_SUCC);
                        bidInfo.setBidTime(new Date());
                        bidInfo.setLoanId(loanId);
                        bidInfo.setUid(uid);
                        bidInfoMapper.insertBidInfo(bidInfo);

                        //7.判断产品是否满标， 判断剩余可投资金额是否为 0
                        LoanInfo dbLoanInfo =  loanInfoMapper.selectById(loanId);
                        if( dbLoanInfo.getLeftProductMoney().intValue() == 0 ){
                            // 8如果满标，更新产品的为满标
                            rows = loanInfoMapper.updateStatusAndFullTime(loanId);
                            if( rows < 1){
                                log.debug("micr-dataservice|invest|update|productStatus|"+uid+"|更新0行记录");
                                throw new RuntimeException("用户购买理财产品，更新产品是满标状态失败");
                            }
                        }
                        //成功
                        result.setResult(1,"投资成功");
                    } else {
                        //投资金额不正确
                        result.setResult(5,"投资金额不满足要求");
                    }
                } else {
                    //产品不存在
                    result.setResult(4,"产品不可投资");
                }
            } else {
                //资金余额不足
                result.setResult(3,"资金余额不足，请充值");
            }
        } else {
            //没有资金账户
            result.setResult(2,"资金账户不可用");
        }
        log.debug("micr-dataservice|invest|end|"+uid+"|"+loanId+"|"+result.getResult()+"|"+result.getMessage());
        return result;
    }




    @Override
    public BigDecimal querySumBidMoney() {
        BigDecimal sumBidMoney = bidInfoMapper.selectSumBidMoney();
        return sumBidMoney;
    }

    /**
     * 根据产品id,查询最近的3条投资记录
     * @param loanId 产品id
     * @return
     */
    @Override
    public List<BidLoanInfo> queryBidListByLoanId(Integer loanId) {
        List<BidLoanInfo> bidLoanInfoList = new ArrayList<>();
        if( loanId != null && loanId > 0 ){
            bidLoanInfoList = bidInfoMapper.selectBidList(loanId);
        }
        return bidLoanInfoList;
    }

    /**
     * @param uid       用户id
     * @param pageNo    页号
     * @param pageSize  页的大小
     * @return  投资记录(产品名称)
     */
    @Override
    public List<BidLoanInfo> queryBidListByUid(Integer uid,
                                               Integer pageNo,
                                               Integer pageSize) {
        List<BidLoanInfo>  lists = new ArrayList<>();
        if( uid !=null ){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);

            int offSet = (pageNo - 1) * pageSize;
            lists = bidInfoMapper.selectBidListByUid(uid,offSet,pageSize);
        }
        return lists;
    }

    /**
     * 某个用户的投资总记录数量
     * @param uid 用户id
     */
    @Override
    public Integer countBidRecordByUid(Integer uid) {
        Integer bidRecords  = bidInfoMapper.selectCountBidByUid(uid);
        return bidRecords;
    }


}
