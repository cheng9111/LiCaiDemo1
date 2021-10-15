package com.bjpowernode.web.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.config.JdwxRealNameConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Slf4j
@Service
public class RealNameService {

    @Resource
    private JdwxRealNameConfig nameConfig;

    //实名认证
    public boolean realName(String phone, String card,String name){
        log.debug("micr-web实名认证-开始:card="+card+"|name="+name);
        boolean flag = false;
        //调用第三方接口
        CloseableHttpClient client = HttpClients.createDefault();
        //https://way.jd.com/youhuoBeijing/test?cardNo=150429198407091210
        // &realName=乐天磊&appkey=您申请的APPKEY
        String url = nameConfig.getUrl()+"?cardNo="+card
                    +"&realName="+name
                    +"&appkey="+nameConfig.getAppkey();
        log.debug("micr-web实名认证-请求url="+url);

        HttpGet get  = new HttpGet(url);
        try{
            CloseableHttpResponse response = client.execute(get);
            if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String json  = EntityUtils.toString(response.getEntity());

                json = "{\n" +
                        "    \"code\": \"10000\",\n" +
                        "    \"charge\": false,\n" +
                        "    \"remain\": 1305,\n" +
                        "    \"msg\": \"查询成功\",\n" +
                        "    \"result\": {\n" +
                        "        \"error_code\": 0,\n" +
                        "        \"reason\": \"成功\",\n" +
                        "        \"result\": {\n" +
                        "            \"realname\": \""+name+"\",\n" +
                        "            \"idcard\": \""+card+"\",\n" +
                        "            \"isok\": true\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";

                log.debug("micr-web实名认证-调用接口返回="+json);
                JSONObject jsonObject = JSONObject.parseObject(json);
                if( "10000".equals(jsonObject.getString("code"))){
                    JSONObject resultObject = jsonObject.getJSONObject("result");
                    if( resultObject != null ){
                        JSONObject subResultObject = resultObject.getJSONObject("result");
                        if( subResultObject != null){
                            flag = subResultObject.getBoolean("isok");
                        }
                    }
                }
            } else  {
                log.debug("micr-web实名认证-调用接口返回="+response.getStatusLine().getStatusCode());
            }
            client.close();
        }catch (Exception e){
            flag = false;
            e.printStackTrace();
        }
        log.debug("micr-web实名认证-完成:card="+card+"|name="+name+"|flag="+flag);
        log.info("micr-web实名认证-realname-phone="+phone+"|card="+card+"|name="+name+"|flag="+flag);
        return flag;
    }
}
