package com.bjpowernode.service.impl;

import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.mapper.BidInfoMapper;
import com.bjpowernode.mapper.FinanceAccountMapper;
import com.bjpowernode.mapper.IncomeRecordMapper;
import com.bjpowernode.mapper.LoanInfoMapper;
import com.bjpowernode.model.BidInfo;
import com.bjpowernode.model.IncomeRecord;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.pojo.IncomeLoanInfo;
import com.bjpowernode.service.IncomeService;
import com.bjpowernode.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@DubboService(interfaceClass = IncomeService.class,version = "1.0")
public class IncomeServiceImpl implements IncomeService {

    @Resource
    private IncomeRecordMapper incomeRecordMapper;

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public List<IncomeLoanInfo> queryIncomeListByUid(Integer uid, Integer pageNo, Integer pageSize) {
        List<IncomeLoanInfo> incomeLoanInfoList = new ArrayList<>();
        if( uid !=null && uid > 0 ){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);

            int offSet = (pageNo -1 ) * pageSize;
            incomeLoanInfoList = incomeRecordMapper.selectIncomeListByUid(uid,offSet,pageSize);
        }
        return incomeLoanInfoList;
    }

    @Override
    public Integer queryIncomeRecordByUid(Integer uid) {
        Integer counts  = 0;
        if( uid != null && uid > 0 ){
            counts = incomeRecordMapper.selectCountRecordByUid(uid);
        }
        return counts;
    }

    /**
     * ??????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void generateIncomePlan() {
        log.debug("micr-dataservice|generateIncomePlan|begin|"+new Date());
        //1.???????????????????????????
        Date curDate = new Date();//2021-08-20 11:19:20
        //2021-08-19 00:00:00
        Date beginDate =DateUtils.truncate( DateUtils.addDays(curDate,-1),Calendar.DATE);
        //2021-08-20 00:00:00
        Date endDate = DateUtils.truncate(curDate, Calendar.DATE);
        System.out.println("beginDate="+beginDate+",endDate="+endDate);
        //1 ??????????????????
        List<LoanInfo> manBiaoList = loanInfoMapper.selectManBiaoList(beginDate,endDate);

        log.debug("micr-dataservice|generateIncomePlan|loan-records|"+manBiaoList.size());
        //2.???????????? = ???????????? * ?????? * ??????
        BigDecimal incomeMoney = null;  //??????
        BigDecimal dayRate = null;      //?????????
        Date incomeDate = null;         //????????????
        Date fullTime = null ;          //????????????
        int ptype = -1;
        int cycle = -1;
        for(LoanInfo loanInfo : manBiaoList){
            log.debug("micr-dataservice|generateIncomePlan|loan|"+loanInfo.getId());
            //?????????
            dayRate = loanInfo.getRate()
                    .divide(new BigDecimal("100"), 10,RoundingMode.HALF_UP)
                    .divide(new BigDecimal("365"),10,RoundingMode.HALF_UP);
            //??????
            cycle = loanInfo.getCycle();
            //????????????
            fullTime = loanInfo.getProductFullTime();

            //????????????
            ptype = loanInfo.getProductType().intValue();
            //3.???????????????????????????
            List<BidInfo> bidList = bidInfoMapper.selectListByLoanId(loanInfo.getId());
            for(BidInfo bid: bidList){
                log.debug("micr-dataservice|generateIncomePlan|bid|"+bid.getId());
                if( ptype == LicaiConstants.PRODUCT_TYPE_XINSHOUBAO){
                    //????????? ??????
                    incomeMoney = bid.getBidMoney().multiply(dayRate).multiply(new BigDecimal(cycle));
                    //????????????
                    incomeDate = DateUtils.addDays(fullTime,cycle + 1);
                } else {
                    //?????? ?????????
                    incomeMoney = bid.getBidMoney().multiply(dayRate.multiply(new BigDecimal(cycle * 30 )));
                    incomeDate = DateUtils.addMonths(DateUtils.addDays(fullTime,1),cycle);
                }
                //??????????????????
                IncomeRecord ir  = new IncomeRecord();
                ir.setBidId(bid.getId());
                ir.setBidMoney(bid.getBidMoney());
                ir.setIncomeDate(incomeDate);
                ir.setIncomeMoney(incomeMoney);
                ir.setIncomeStatus(LicaiConstants.INCOME_STATUS_PLAN);
                ir.setLoanId(bid.getLoanId());
                ir.setUid(bid.getUid());
                incomeRecordMapper.insertIncomeRecord(ir);
            }

            //????????????????????????
            int rows = loanInfoMapper.updateStatusGeneratePlan(loanInfo.getId());
            if( rows < 1){
                throw new RuntimeException("?????????????????????2??????");
            }
            log.debug("micr-dataservice|generateIncomePlan|end|"+new Date());
        }
    }

    //????????????
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void generateIncomeBack() {

        log.debug("micr-dateservice|generateIncomeBack|begin|"+new Date());
        int rows = 0;
        //0.??????????????????????????? 1000
        //1.???????????????????????????
        // uid ,bidMoney, incomeMoney
        List<IncomeRecord> recordList = incomeRecordMapper.selectExpiredIncome();
        log.debug("micr-dateservice|generateIncomeBack|records|"+recordList.size());
        for(IncomeRecord ir : recordList){
            log.debug("micr-dateservice|generateIncomeBack|income-uid|"+ir.getUid()+"|"+ ir.getBidMoney()+"|"+ir.getIncomeMoney());
            //2.??????????????????
            rows =financeAccountMapper.updateAddAvailableMoneyByIncome(ir.getUid(),
                                            ir.getBidMoney(),ir.getIncomeMoney());
            if(rows < 1){
                throw new RuntimeException("??????????????????????????????????????????");
            }

            //3.?????????????????????
            rows  = incomeRecordMapper.updateStatusBack(ir.getUid());
            if( rows < 1 ){
                throw new RuntimeException("????????????????????????????????????1??????");
            }
        }
        log.debug("micr-dateservice|generateIncomeBack|end|"+new Date());
    }
}
