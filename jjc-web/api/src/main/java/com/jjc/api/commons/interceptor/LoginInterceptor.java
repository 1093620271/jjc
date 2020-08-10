package com.jjc.api.commons.interceptor;


import com.alibaba.fastjson.JSON;
import com.jjc.api.commons.aop.AllowAnonymous;
import com.jjc.api.commons.aop.ApiTokenAuthorize;
import com.jjc.api.commons.entity.ResponseModel;
import com.jjc.comm.common.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * 登录拦截器
 * @author huoquan
 * @date 2018/12/10.
 */
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    private static final String OPTIONS = "OPTIONS";
    /**
     * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，
     * SpringMVC中的Interceptor拦截器是链式的，可以同时存在
     * 多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行
     * ，而且所有的Interceptor中的preHandle方法都会在
     * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的
     * ，这种中断方式是令preHandle的返 回值为false，当preHandle的返回值为false的时候整个请求就结束了。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 允许跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");//允许类型全部
        if(OPTIONS.equalsIgnoreCase(request.getMethod())){
            return true;  // 如果是跨域中的OPTIONS请求，直接返回 add by huoquan 20200409
        }
        String url = request.getRequestURI().substring(request.getContextPath().length());
        String params=getParamString(request.getParameterMap());
        if(url.equals("/swagger-ui.html")){
            return  true;
        }
        logger.info("请求url打印::::"+request.getRequestURL()+"?"+params);
        //判断是否有注解授权验证
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AllowAnonymous allowAnonymous=method.getAnnotation(AllowAnonymous.class);
        if(allowAnonymous!=null){
            //如果有这个注解，则不需要验证
            return true;
        }
        ApiTokenAuthorize webApiTokenAuthorize=method.getAnnotation(ApiTokenAuthorize.class);
        if(webApiTokenAuthorize!=null){
            //验证header的token
            //final String token = request.getHeader("auth");
            String token = request.getHeader("token");
            //判断是否登录授权，没有登录则退出
            logger.info("=============需要判断登录授权=============,token:"+token);
            if(ToolUtil.isEmpty(token)){
                token = request.getParameter("auth");
                if(ToolUtil.isEmpty(token)){
                    //授权验证失败，请检查是否携带有效的token
                    tokenFailure(response,"授权验证失败，当前请求头缺失auth的验证信息");
                    return false;
                }
            }
        }
        return true;
    }

    private String getParamString(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();
        try{
            for(Map.Entry<String, String[]> e:map.entrySet()){
                sb.append(e.getKey()).append("=");
                String[] value = e.getValue();
                if(value != null && value.length == 1){
                    sb.append(value[0]).append("&");
                }else{
                    sb.append(Arrays.toString(value)).append("&");
                }
            }
            if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);
            }
        }catch (Exception e){
            logger.error("解析url的参数失败:",e);
        }
        return sb.toString();
    }

    private void tokenFailure(HttpServletResponse response, String msg){
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            ResponseModel responseModel=new ResponseModel();
            responseModel.setSuccess(false);
            responseModel.setErrorMessge(msg);
            responseModel.setCode(1);
            out.println(JSON.toJSONString(responseModel));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(out!=null){
                out.close();
            }
        }
    }

}
