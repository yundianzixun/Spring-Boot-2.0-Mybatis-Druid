package com.itunion.springbootstarterdruid.config;

import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

@WebFilter(filterName = "druidStatFilter", urlPatterns = "/*", asyncSupported = true,
        initParams = {
                @WebInitParam(name = "exclusions", value = "/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*")//忽略资源
        }
)
public class DruidStatFilter extends WebStatFilter{
}

//@WebFilter(filterName = "druidWebStatFilter", urlPatterns = "/*",
//        initParams = {
//                @WebInitParam(name = "exclusions", value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")//忽略资源
//        })
//public class DruidStatFilter extends WebStatFilter {
//
//}