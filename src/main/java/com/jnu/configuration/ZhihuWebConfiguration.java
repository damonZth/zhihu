package com.jnu.configuration;

import com.jnu.interceptor.LoginRrquredInterceptor;
import com.jnu.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Damon on 2017/8/14.
 */
@Component
public class ZhihuWebConfiguration extends WebMvcConfigurerAdapter{

    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRrquredInterceptor loginRrquredInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRrquredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }

}
