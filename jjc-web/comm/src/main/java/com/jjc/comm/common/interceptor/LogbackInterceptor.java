package com.jjc.comm.common.interceptor;


import com.jjc.comm.common.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通过MDC和操作traceId实现日志链路追踪
 * @author huoquan
 * @date 2019/12/06.
 */
public class LogbackInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LogbackInterceptor.class);
    /**
     * 线程追踪ID
     */
    public final static String SESSION_TOKEN_KEY = "traceId";
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
        MDC.put(SESSION_TOKEN_KEY, ToolUtil.getUUID());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 最后执行MDC删除
        MDC.remove(SESSION_TOKEN_KEY);
    }
}
