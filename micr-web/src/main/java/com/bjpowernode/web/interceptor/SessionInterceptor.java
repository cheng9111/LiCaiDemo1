package com.bjpowernode.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.constants.ErrCode;
import com.bjpowernode.constants.LicaiConstants;
import com.bjpowernode.pojo.RespObject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

//判断用户是否登录
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object user = session.getAttribute(LicaiConstants.SESSION_USER);
        if(user == null){

            System.out.println("================执行了拦截器SessionInterceptor================");

            //区分请求是访问页面的，还是ajax的
            String ajaxRequest = request.getHeader("X-Requested-With");
            if("XMLHttpRequest".equals(ajaxRequest)){
                //ajax请求， 返回数据
                response.setContentType("application/json;charset=utf-8");
                RespObject ro  = new RespObject(ErrCode.ERR_NOT_LOGIN,"没有登录","");
                String json = JSONObject.toJSONString(ro);
                PrintWriter out = response.getWriter();
                out.println(json);
                out.flush();
                out.close();

            } else{
                //没有登录， 跳转到登录页面
                response.sendRedirect( request.getContextPath() + "/loan/page/login");
            }

            return false;
        }
        return true;
    }
}
