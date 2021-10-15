package com.bjpowernode.task;

import com.bjpowernode.service.IncomeService;
import com.bjpowernode.util.HttpClientUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TaskManager {

    @Value("${pay.kqUrl}")
    private String payKqQueryUrl;

    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    private IncomeService incomeService;

    /**
     * 定时任务的方法：
     * 1.public方法
     * 2.没有参数
     * 3.没有返回值
     *
     * @Scheduled:放在方法的上面，表示方法是定时任务要执行的功能
     *   cron: 时间表达式
     */
    // @Scheduled(cron = "*/20 * * * * ?")
    public void testCron(){
        System.out.println("定时任务："+ new Date());
    }

    //凌晨1点整执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateIncomePlan(){
        //调用数据dataservice
        incomeService.generateIncomePlan();
    }

    //凌晨2点整执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateIncomeBack(){
        //调用数据dataservice
        incomeService.generateIncomeBack();
    }

    //定时任务，调用快钱
    @Scheduled(cron = "*/20 * * * * ?")
    public void handlerMicrPayKqQuery(){
        try {
            HttpClientUtils.doGet(payKqQueryUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
