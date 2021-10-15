package com.bjpowernode.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.constants.LicaiRedisKey;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.pojo.RechargeResult;
import com.bjpowernode.util.kq.HttpUtil;
import com.bjpowernode.util.kq.Pkipair;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class KuqiQianService {

    Logger logger  = LoggerFactory.getLogger(KuqiQianService.class);

    @DubboReference(version = "1.0")
    private RechargeService rechargeService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //创建充值记录
    public int createRechargeRecord(Integer uid, String money,Map<String,String> data){
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setChannel("kq");
        rechargeRecord.setRechargeDesc("使用快钱充值");
        rechargeRecord.setRechargeMoney(new BigDecimal(money));
        rechargeRecord.setRechargeNo(data.get("orderId"));
        rechargeRecord.setRechargeStatus(LicaiConstants.RECHARGE_PROCESSING); // 0
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setUid(uid);
        int rows  = rechargeService.addRechargeRecored(rechargeRecord);
        return rows;
    }

    //把orderId存放到redis
    public void addOrderIdToRedis(Map<String,String> data){
        stringRedisTemplate.opsForZSet().add(LicaiRedisKey.PAY_KQ_ORDER_LIST,
                data.get("orderId"),
                new Date().getTime());
    }

    //处理异步通知
    public RechargeResult handlerNotify(HttpServletRequest request){
        logger.debug("kuaiqian|notify|begin|"+ new Date());
        String merchantAcctId = request.getParameter("merchantAcctId");
        String version = request.getParameter("version");
        String language = request.getParameter("language");
        String signType = request.getParameter("signType");
        String payType = request.getParameter("payType");
        String bankId = request.getParameter("bankId");
        //商家订单号
        String orderId = request.getParameter("orderId");
        String orderTime = request.getParameter("orderTime");
        //商家订单金额
        String orderAmount = request.getParameter("orderAmount");
        String bindCard="";
        if(request.getParameter("bindCard")!=null){
            bindCard = request.getParameter("bindCard");
        }

        String bindMobile="";
        if(request.getParameter("bindMobile")!=null){
            bindMobile = request.getParameter("bindMobile");
        }
        String dealId = request.getParameter("dealId");
        String bankDealId = request.getParameter("bankDealId");
        String dealTime = request.getParameter("dealTime");
        //实际支付金额
        String payAmount = request.getParameter("payAmount");
        String fee = request.getParameter("fee");
        String ext1 = request.getParameter("ext1");
        String ext2 = request.getParameter("ext2");
        //支付结果 10成功， 11失败
        String payResult = request.getParameter("payResult");
        String aggregatePay = request.getParameter("aggregatePay");
        String errCode = request.getParameter("errCode");
        //快钱的签名值
        String signMsg = request.getParameter("signMsg");

        String merchantSignMsgVal = "";
        merchantSignMsgVal = appendParam(merchantSignMsgVal,"merchantAcctId", merchantAcctId,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "version",version,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "language",language,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType",signType,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType",payType,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId",bankId,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId",orderId,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime",orderTime,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount",orderAmount,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bindCard",bindCard,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bindMobile",bindMobile,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId",dealId,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId",bankDealId,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime",dealTime,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount",payAmount,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult",payResult,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "aggregatePay",aggregatePay,null);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode",errCode,null);
        logger.debug("kuaiqian|notify|msgval|"+merchantSignMsgVal);

        Pkipair pki = new Pkipair();
        //验签使用的
        boolean flag = pki.enCodeByCer(merchantSignMsgVal, signMsg);

        logger.debug("kuaiqian|notify|encodebycer|orderid|"+orderId+"|"+flag);
        //判断验签
        RechargeResult result = null;
        if( flag ){
            //验证通过，可以处理业务逻辑
            //1. 处理数据库 ： 根据订单号，查询订单是否是商家自己的。 订单是否已经处理过，  判断金额是否正确
            //               订单是否成功，成功：给用户增加金额， 修改充值记录状态是1
            //               订单失败： 修改充值记录状态是2
            result =  rechargeService.handlerRechargeNotify(orderId,payResult,payAmount,"kq");
            logger.debug("kuaiqian|notify|result|"+result.getResult()+"|"+result.getMessage());
            logger.info("kuaiqian|recharge|"+payResult+"|"+orderId+"|"+payAmount+"|"+result.getResult()+"|"+result.getMessage());

        } else {
            //验签失败
            logger.info("kuaiqian|notify|encodebycer|orderid|"+orderId+"|fail");
            //发送邮件。开发人员，客服人员
            result = new RechargeResult(6,orderId+"快钱异步通知验签失败");
        }

        //删除redis里面的记录
        System.out.println("======================从redis删除=="+orderId);
        stringRedisTemplate.opsForZSet().remove(LicaiRedisKey.PAY_KQ_ORDER_LIST,orderId);

        return result;
    }


    //从redis获取 orderId
    public void getRedisOrderId(){
        Set<ZSetOperations.TypedTuple<String>> sets = stringRedisTemplate.opsForZSet()
                           .rangeWithScores(LicaiRedisKey.PAY_KQ_ORDER_LIST, 0, -1);

        sets.forEach( c->{
            System.out.println(c.getValue()+","+ c.getScore().longValue());
            invokeKqQueryAPI(c.getValue());
        });

    }


    //调用快钱的查询api接口
    public void invokeKqQueryAPI(String orderId){
        Map<String, Object> request = new HashMap<String, Object>();
        //固定值：1代表UTF-8;
        String inputCharset = "1";
        //固定值：v2.0 必填
        String version = "v2.0";
        //1代表Md5，2 代表PKI加密方式  必填
        String signType = "2";
        //人民币账号 membcode+01  必填
        String merchantAcctId = "1001214035601";
        //固定值：0 按商户订单号单笔查询，1 按交易结束时间批量查询必填
        String queryType = "0";
        //固定值：1	代表简单查询 必填
        String queryMode = "1";
        //数字串，
       // String startTime = "20210826000000";
        //数字
       // String endTime = "20210826235959";
        String startTime="";
        String endTime = "";
        String requestPage = "";
        String key = "27YKWKBKHT2IZSQ4";

        request.put("inputCharset", inputCharset);
        request.put("version", version);
        request.put("signType", signType);
        request.put("merchantAcctId", merchantAcctId);
        request.put("queryType", queryType);
        request.put("queryMode", queryMode);
        request.put("startTime", startTime);
        request.put("endTime", endTime);
        request.put("requestPage", requestPage);
        request.put("orderId", orderId);

        String message="";
        message = appendParam(message,"inputCharset",inputCharset,null);
        message = appendParam(message,"version",version,null);
        message = appendParam(message,"signType",signType,null);
        message = appendParam(message,"merchantAcctId",merchantAcctId,null);
        message = appendParam(message,"queryType",queryType,null);
        message = appendParam(message,"queryMode",queryMode,null);
        message = appendParam(message,"startTime",startTime,null);
        message = appendParam(message,"endTime",endTime,null);
        message = appendParam(message,"requestPage",requestPage,null);
        message = appendParam(message,"orderId",orderId,null);
        message = appendParam(message,"key",key,null);

        String response = "";
        try {
            Pkipair pki = new Pkipair();
            String sign = pki.signMsg(message);
            request.put("signMsg", sign);

            System.out.println("请求json串===" + JSON.toJSONString(request));
            String reqUrl = "https://sandbox.99bill.com/gatewayapi/gatewayOrderQuery.do";

            response = HttpUtil.doPostJsonRequestByHttps(JSON.toJSONString(request), reqUrl, 3000, 8000);
            System.out.println("返回json串==="+response);
            //Map<String,Object> m = new HashMap<String, Object>();
            //m = JSON.parseObject(response, Map.class);
            //解析json数据， 判断订单的充值结果
            JSONObject json = JSONObject.parseObject(response);
            if( json != null ){
                JSONArray orderDetail = json.getJSONArray("orderDetail");
                if( orderDetail != null){
                    JSONObject orderJson = orderDetail.getJSONObject(0);
                    String payAmount = orderJson.getString("payAmount");
                    String payResult = orderJson.getString("payResult");
                    rechargeService.handlerRechargeNotify(orderId,payResult,payAmount,"kq");
                }
                //从redis删除处理的订单号
                stringRedisTemplate.opsForZSet().remove(LicaiRedisKey.PAY_KQ_ORDER_LIST,orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    //生成快钱表单需要的数据
    public Map<String,String> getFormData(Integer uid,String phone,String money) throws Exception{
        //人民币网关账号，该账号为11位人民币网关商户编号+01,该参数必填。
        String merchantAcctId = "1001214035601";//
        //编码方式，1代表 UTF-8; 2 代表 GBK; 3代表 GB2312 默认为1,该参数必填。
        String inputCharset = "1";
        //接收支付结果的页面地址，该参数一般置为空即可。
        String pageUrl = "";
        //服务器接收支付结果的后台地址，该参数务必填写，不能为空。
        //商家的地址， 盈利宝平台上的一个地址， 自己提供
        String bgUrl = "http://47.113.198.114:9999/pay/kq/notify";
        //网关版本，固定值：v2.0,该参数必填。
        String version =  "v2.0";
        //语言种类，1代表中文显示，2代表英文显示。默认为1,该参数必填。
        String language =  "1";
        //签名类型,该值为4，代表PKI加密方式,该参数必填。
        String signType =  "4";
        //支付人姓名,可以为空。
        String payerName= "";
        //支付人联系类型，1 代表电子邮件方式；2 代表手机联系方式。可以为空。
        String payerContactType =  "2";
        //支付人联系方式，与payerContactType设置对应，payerContactType为1，则填写邮箱地址；payerContactType为2，则填写手机号码。可以为空。
        String payerContact = phone;
        //指定付款人，可以为空
        String payerIdType =  "3";
        //付款人标识，可以为空
        String payerId =  String.valueOf(uid);;
        //付款人IP，可以为空
        String payerIP =  "192.168.1.1";
        //商户订单号，以下采用时间来定义订单号，商户可以根据自己订单号的定义规则来定义该值，不能为空。
        String orderId = "KQ"+ makeOrderId();
        //订单金额，金额以“分”为单位，商户测试以1分测试即可，切勿以大金额测试。该参数必填。
        //0.01
        BigDecimal bigMoney = new BigDecimal(money).multiply(new BigDecimal("100"));
        String orderAmount = String.valueOf(bigMoney.intValue());

        //订单提交时间，格式：yyyyMMddHHmmss，如：20071117020101，不能为空。
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        //快钱时间戳，格式：yyyyMMddHHmmss，如：20071117020101， 可以为空
        String orderTimestamp= new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());;
        //商品名称，可以为空。
        String productName= "Apple";
        //商品数量，可以为空。
        String productNum = "1";
        //商品代码，可以为空。
        String productId = "10000";
        //商品描述，可以为空。
        String productDesc = "Apple";
        //扩展字段1，商户可以传递自己需要的参数，支付完快钱会原值返回，可以为空。
        String ext1 = "";
        //扩展自段2，商户可以传递自己需要的参数，支付完快钱会原值返回，可以为空。
        String ext2 = "";
        //支付方式，一般为00，代表所有的支付方式。如果是银行直连商户，该值为10-1或10-2，必填。
        String payType = "00";
        //银行代码，如果payType为00，该值可以为空；如果payType为10-1或10-2，该值必须填写，具体请参考银行列表。
        String bankId = "";
        //同一订单禁止重复提交标志，实物购物车填1，虚拟产品用0。1代表只能提交一次，0代表在支付不成功情况下可以再提交。可为空。
        String redoFlag = "0";
        //快钱合作伙伴的帐户号，即商户编号，可为空。
        String pid = "";

        Map<String,String> data = new HashMap<>();
        // signMsg 签名字符串 不可空，生成加密签名串
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset,data);
        signMsgVal = appendParam(signMsgVal, "pageUrl", pageUrl,data);
        signMsgVal = appendParam(signMsgVal, "bgUrl", bgUrl,data);
        signMsgVal = appendParam(signMsgVal, "version", version,data);
        signMsgVal = appendParam(signMsgVal, "language", language,data);
        signMsgVal = appendParam(signMsgVal, "signType", signType,data);
        signMsgVal = appendParam(signMsgVal, "merchantAcctId",merchantAcctId,data);
        signMsgVal = appendParam(signMsgVal, "payerName", payerName,data);
        signMsgVal = appendParam(signMsgVal, "payerContactType",payerContactType,data);
        signMsgVal = appendParam(signMsgVal, "payerContact", payerContact,data);
        signMsgVal = appendParam(signMsgVal, "payerIdType", payerIdType,data);
        signMsgVal = appendParam(signMsgVal, "payerId", payerId,data);
        signMsgVal = appendParam(signMsgVal, "payerIP", payerIP,data);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId,data);
        signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount,data);
        signMsgVal = appendParam(signMsgVal, "orderTime", orderTime,data);
        signMsgVal = appendParam(signMsgVal, "orderTimestamp", orderTimestamp,data);
        signMsgVal = appendParam(signMsgVal, "productName", productName,data);
        signMsgVal = appendParam(signMsgVal, "productNum", productNum,data);
        signMsgVal = appendParam(signMsgVal, "productId", productId,data);
        signMsgVal = appendParam(signMsgVal, "productDesc", productDesc,data);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1,data);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2,data);
        signMsgVal = appendParam(signMsgVal, "payType", payType,data);
        signMsgVal = appendParam(signMsgVal, "bankId", bankId,data);
        signMsgVal = appendParam(signMsgVal, "redoFlag", redoFlag,data);
        signMsgVal = appendParam(signMsgVal, "pid", pid,data);

        System.out.println("===signMsgVal==="+signMsgVal);
        Pkipair pki = new Pkipair();
        String signMsg = pki.signMsg(signMsgVal);

        //必须添加signMsg参数
        data.put("signMsg",signMsg);
        return data;
    }

    private String appendParam(String returns, String paramId, String paramValue,Map<String,String> map) {
        if (returns != "") {
            if (paramValue != "" && paramValue != null) {
                returns += "&" + paramId + "=" + paramValue;
                if(map != null){
                    map.put(paramId,paramValue);
                }

            }
        } else {
            if (paramValue != "" && paramValue != null) {
                returns = paramId + "=" + paramValue;
                if(map != null){
                    map.put(paramId,paramValue);
                }

            }
        }
        return returns;
    }

    //生成商家的订单号
    private String makeOrderId(){
        String date = DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS");//2021080231041201231
        //redis的incr命令，
        String orderId = date + stringRedisTemplate.opsForValue().increment(LicaiRedisKey.PAY_KQ_ORDERID_SEQ);
        return  orderId;
    }
}
