package com.bjpowernode.service.impl;

import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.mapper.FinanceAccountMapper;
import com.bjpowernode.mapper.RechargeRecordMapper;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.pojo.RechargeResult;
import com.bjpowernode.service.RechargeService;
import com.bjpowernode.util.CommonUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = RechargeService.class,version = "1.0")
public class RechargeServiceImpl implements RechargeService {

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    /**
     * 查询某个用户充值记录
     * @param uid        用户id
     * @param pageNo     页号
     * @param pageSize   页大小
     * @return 充值记录
     */
    @Override
    public List<RechargeRecord> queryRechargeListByUid(Integer uid, Integer pageNo, Integer pageSize) {
        List<RechargeRecord> rechargeRecordList = new ArrayList<>();
        if( uid != null ){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            int offSet = (pageNo - 1) * pageSize;
            rechargeRecordList = rechargeRecordMapper.selectRechargeListByUid(uid,offSet, pageSize);
        }
        return rechargeRecordList;
    }

    /**
     * 用户充值记录总数
     * @param uid 用户id
     * @return 记录总数
     */
    @Override
    public Integer queryRechargeRecordsByUid(Integer uid) {
        Integer records  = 0;
        if( uid != null){
            records = rechargeRecordMapper.selectRecordsByUid(uid);
        }
        return records;
    }

    /**
     * 创建充值记录
     * @param rechargeRecord
     * @return
     */
    @Override
    public int addRechargeRecored(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertRecharge(rechargeRecord);
    }

    /**
     * @param orderId    订单号
     * @param payResult  充值结果 10 成功， 11 失败
     * @param payAmount  充值金额 分
     * @param channel    渠道，使用那个充值， kq：快钱
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RechargeResult handlerRechargeNotify(String orderId, String payResult,
                                                String payAmount, String channel) {

        int rows = 0;
        int fen = 0;

        RechargeResult rechargeResult = new RechargeResult(0,"未知问题");
        //1.成功处理
        //2.处理失败
        //查询订单
        if( orderId != null ){
           RechargeRecord recharge =  rechargeRecordMapper.selectRechargeByRechargeNo(orderId);
           if( recharge != null){
                //判断订单是否已经处理过
               if( recharge.getRechargeStatus() == LicaiConstants.RECHARGE_PROCESSING){
                    //订单可以处理，判断金额是否一致
                   fen = (recharge.getRechargeMoney().multiply(new BigDecimal("100"))).intValue();
                   if( fen == Integer.parseInt(payAmount)) {
                       //处理处理充值了 ，成功，或者失败
                       int status  = 0;
                       if("10".equals(payResult)){
                           //成功 1）.更新资金账户 2）修改充值记录的状态
                           rows = financeAccountMapper.updateAddAvailableMoneyByRecharge(
                                                           recharge.getUid(),
                                                           recharge.getRechargeMoney());
                           if( rows < 1 ){
                               throw new RuntimeException("处理充值异步通知，更新账户资金失败");
                           }
                           status = LicaiConstants.RECHARGE_SUCCESS;
                       } else {
                           //失败 ,可能还有其他的代码
                           status = LicaiConstants.RECHARGE_FAILURE;
                       }
                       //修改充值记录的状态
                       rows = rechargeRecordMapper.updateStatus(recharge.getId(),status);
                       if( rows < 1){
                           throw new RuntimeException("处理充值异步通知，更新充值记录状态失败");
                       }

                       rechargeResult = new RechargeResult(status,orderId+"订单处理完成");

                   } else {
                       //金额不一致
                       rechargeResult = new RechargeResult(5,orderId+"订单金额和支付金额不一致");
                   }
               } else {
                   rechargeResult = new RechargeResult(4,orderId+"订单已经处理过，不能在处理了");
               }
           } else {
               rechargeResult = new RechargeResult(3,orderId+"商家没有充值记录");
           }
        }
        return rechargeResult;
    }
}
