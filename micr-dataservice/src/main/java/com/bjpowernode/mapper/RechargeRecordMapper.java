package com.bjpowernode.mapper;

import com.bjpowernode.model.RechargeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeRecordMapper {
   //分页查询 用户的充值记录
   List<RechargeRecord> selectRechargeListByUid(@Param("uid") Integer uid,
                                                @Param("offSet") Integer offSet,
                                                @Param("rows") Integer rows);

   //用户的充值记录总数
   Integer selectRecordsByUid(@Param("uid") Integer uid);

   //创建充值记录
   int insertRecharge(RechargeRecord rechargeRecord);

   //根据订单号，查询充值记录
   RechargeRecord selectRechargeByRechargeNo(@Param("rechargeNo") String orderId);

   //修改状态
   int updateStatus(@Param("rechargeId") Integer rechargeId, @Param("status") Integer status);
}