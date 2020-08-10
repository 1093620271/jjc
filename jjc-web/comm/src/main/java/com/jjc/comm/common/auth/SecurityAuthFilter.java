package com.jjc.comm.common.auth;


import com.jjc.comm.common.sys.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

;

/**
 * 安全认证拦截器
 * @author huoquan
 * @date 2018/11/8.
 */
//@Component
public class SecurityAuthFilter implements Filter {

    private static Logger LOGGER = LoggerFactory.getLogger(SecurityAuthFilter.class);

    public final static String AUTHORIZATION = "token";

    @Autowired
    private TokenTask tokenTask;

    @Autowired
    private SecurityAuthProperties authProperties;

    public SecurityAuthFilter() {

    }

    public SecurityAuthFilter(SecurityAuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (this.isContain(httpServletRequest.getRequestURI(), authProperties.getUriList())) {
            String authorization = httpServletRequest.getHeader(AUTHORIZATION);
            boolean token = tokenTask.checkToken(authorization);
            if (!token) {
                LOGGER.warn("token验证失败 remoteAddr:{}, url:{}", httpServletRequest.getRemoteAddr(),
                        httpServletRequest.getRequestURL());
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter pw = resp.getWriter();
                Result result=new Result();
                pw.write("{\n" +
                        "    \"bizData\": null,\n" +
                        "    \"rtnCode\": 403,\n" +
                        "    \"msg\": \"非法请求\",\n" +
                        "}");
                pw.flush();
                pw.close();
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    public boolean isContain(String url, List<String> list) {
        for (String str : list) {
            if (url.indexOf(str.trim()) >= 0) {
                return true;
            }
        }
        return false;
    }
}
