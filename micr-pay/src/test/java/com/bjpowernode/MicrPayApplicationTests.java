package com.bjpowernode;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MicrPayApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void test(){
		String json="{\"recordCount\":\"\",\"pageCount\":\"\",\"orderDetail\":null,\"signMsg\":\"BuicZFIcPdE2rRs%2B8h3%2FwY9VPwUYOnAmZsk0LrioRPt7ahLx3hryik716nDc%2Bz9bj0qMJpYJZr8FhW%2F3LWRXt1u7dvjG0Vl%2F1sMN1rrtaiSYwMfvRvaEnqTGw074FlVbYL4ZdcPo%2Bm8oSJBNd3D3aaJ5zYMPFJAWmQe80IQSMbLNkhbPR5D526gP4Cr0hoMMn7ZtfjTmgxELdyMJfOwdBMjDTbftOHr%2BNoyStdHxNS5TfQjX6jWQ0fDZDqY0fjY28bWxevdqyOUaVdTLB8YtUlsoDr4vsfGa0uVLDshzvU71cpOn6vu0nkUGC3n1wJKSIbjQu032FMDbEvK3IYUl%2Bw%3D%3D\",\"signType\":2,\"pageSize\":\"\",\"merchantAcctId\":\"1001214035601\",\"errCode\":\"31003\",\"currentPage\":\"\",\"version\":\"v2.0\"}";
		JSONArray o = JSONObject.parseObject(json).getJSONArray("orderDetail");
		System.out.println("o==="+ (o == null));

	}

}
