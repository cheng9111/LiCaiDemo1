package com.bjpowernode;

import com.bjpowernode.task.TaskManager;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

//启动dubbo服务
@EnableDubbo
//启动定时任务
@EnableScheduling
@SpringBootApplication
public class TaskApplication {
    public static  void main(String args[]){
        ApplicationContext ctx = SpringApplication.run(TaskApplication.class,args);
        //TaskManager bean = ctx.getBean(TaskManager.class);
        //bean.generateIncomePlan();
        //bean.handlerMicrPayKqQuery();
    }
}
