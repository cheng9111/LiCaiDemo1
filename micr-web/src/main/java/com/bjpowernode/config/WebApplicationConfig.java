package com.bjpowernode.config;

import com.bjpowernode.web.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebApplicationConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("=========WebApplicationConfig========addInterceptors==============");
        //配置拦截器
        //拦截的地址
        String [] addPath = {"/loan/**","/user/account"};
        //排除的地址
        String [] excludePath = {

        };
        SessionInterceptor sessionInterceptor = new SessionInterceptor();
        registry.addInterceptor(sessionInterceptor).addPathPatterns(addPath).excludePathPatterns(excludePath);
    }
}
