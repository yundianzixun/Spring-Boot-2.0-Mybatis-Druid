package com.itunion.springbootstarterdruid.config;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

//@WebServlet(urlPatterns = "/druid/*", asyncSupported = true,
//        initParams = {
//                @WebInitParam(name = "allow", value = ""),
//                @WebInitParam(name = "deny", value = ""),
//                @WebInitParam(name = "loginUsername", value = "admin"),
//                @WebInitParam(name = "loginPassword", value = "123456")
//        })
//public class DruidStatViewServlet {
//}

@WebServlet(urlPatterns = "/druid/*",
        initParams = {
                @WebInitParam(name = "allow", value=""),
                @WebInitParam(name="deny", value=""),// IP黑名单 (存在共同时，deny优先于allow)
                @WebInitParam(name="loginUsername", value="admin"),// 用户名
                @WebInitParam(name="loginPassword", value="123456"),// 密码
                @WebInitParam(name="resetEnable", value="false")// 禁用HTML页面上的“Reset All”功能
        })
public class DruidStatViewServlet extends StatViewServlet {

        private static final long serialVersionUID = 1L;

}
