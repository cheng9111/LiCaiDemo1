package com.bjpowernode.web.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.config.JdwxSmsConfig;
import com.bjpowernode.constants.LicaiRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsService {



    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JdwxSmsConfig jdwxSmsConfig;

    /**
     * @param phone 手机号
     * @return true:发送成功, false 失败
     */
    public boolean sendSms(String phone){
        log.debug("注册发送短信-开始");
        //验证码
        String code = random(6);
        System.out.println("注册发送短信,验证码:"+code);
        //1.调用京东万象,发送短信
        log.debug("注册发送短信-调用第三方接口开始");
        boolean isSend = invokeJdwsSms(phone,code);
        //2.根据发送短信的结果, 确定是否使用redis
        if(isSend == true){
            //把验证码存放redis
            stringRedisTemplate.opsForValue().set(
                    LicaiRedisKey.SMS_REGITER_CODE+phone,
                    code, 3, TimeUnit.MINUTES);
            log.debug("注册发送短信-存储验证码到redis-code:"+code);
        }
        log.debug("注册发送短信-完成");
        return isSend;
    }

    //发送短信
    private boolean invokeJdwsSms(String phone,String code) {
        log.debug("注册发送短信-开始-参数:phone="+phone);
        boolean isSend = false;//不成功

        CloseableHttpClient client = HttpClients.createDefault();
        //【动力金融】你的验证码是：%s，3分钟内有效！

        log.debug("注册发送短信-验证码:"+code);
        String content = String.format(jdwxSmsConfig.getContent(), code);
        log.debug("注册发送短信-内容:"+content);
        //https://way.jd.com/chuangxin/dxjk?
        // mobile=13568813957&content=【创信】你的验证码是：5873，3分钟内有效！&appkey=您申请的APPKEY
        String url = jdwxSmsConfig.getUrl()
                + "?mobile=" + phone
                + "&content=" + content
                + "&appkey=" + jdwxSmsConfig.getAppkey();
        log.debug("注册发送短信-请求参数:"+url);
        HttpGet get = new HttpGet(url);

        try {
            CloseableHttpResponse resp = client.execute(get);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.debug("注册发送短信-调用京东万象接口成功");
                String respJson = EntityUtils.toString(resp.getEntity());

                respJson="{\n" +
                        "    \"code\": \"10000\",\n" +
                        "    \"charge\": false,\n" +
                        "    \"remain\": 1305,\n" +
                        "    \"msg\": \"查询成功\",\n" +
                        "    \"result\": {\n" +
                        "        \"ReturnStatus\": \"Success\",\n" +
                        "        \"Message\": \"ok\",\n" +
                        "        \"RemainPoint\": 420842,\n" +
                        "        \"TaskID\": 18424321,\n" +
                        "        \"SuccessCounts\": 1\n" +
                        "    }\n" +
                        "}";

                log.debug("注册发送短信-调用京东万象接口返回结果:"+respJson);
                //解析json, 使用fastjson
                JSONObject respJsonObject = JSONObject.parseObject(respJson);
                if (respJsonObject != null) {
                    if ("10000".equals(  respJsonObject.getString("code")  )  ) {
                        //获取ReturnStatus
                        //返回是result节点对应的json对象
                        JSONObject resultObject = respJsonObject.getJSONObject("result");
                        if( respJsonObject != null){
                            //发送短信结果, true 发送成, false失败
                            isSend = "Success".equalsIgnoreCase(resultObject.getString("ReturnStatus"));
                        }
                    }
                }
            } else {
                log.debug("注册发送短信-调用京东万象接口失败:"+resp.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("注册发送短信-异常:"+e.getMessage());
            e.printStackTrace();
        }
        log.debug("注册发送短信-完成-结果:isSend="+isSend);
        log.info("注册发送短信结果-phone="+phone+"|result="+isSend);
        return isSend;
    }

    //生成6位随机数
    private String random(int len) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < len; i++) {
            builder.append(threadLocalRandom.nextInt(10));
        }
        return builder.toString();
    }

    //验证短信验证码的值
    public boolean checkCode(String phone, String reqCode){
        boolean flag = false;
        if(StringUtils.isNotEmpty(reqCode)){
            String key  = LicaiRedisKey.SMS_REGITER_CODE + phone;
            String redisCode = stringRedisTemplate.opsForValue().get(key);
            flag = reqCode.equals(redisCode);
        }
        return flag;
    }
}
