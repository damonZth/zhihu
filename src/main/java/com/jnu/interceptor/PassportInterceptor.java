package com.jnu.interceptor;

import com.jnu.dao.LoginTicketDAO;
import com.jnu.dao.UserDAO;
import com.jnu.model.HostHolder;
import com.jnu.model.LoginTicket;
import com.jnu.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by Damon on 2017/8/14.
 * 拦截器
 */
@Component
public class PassportInterceptor implements HandlerInterceptor{

    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HostHolder hostHolder;

    /**
     * 请求开始之前，调用该函数，如果返回false，则请求结束
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        //遍历cookie
        if(httpServletRequest.getCookies() != null){
            for(Cookie cookie : httpServletRequest.getCookies()){
                //寻找下发的cookie
                if(cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if(ticket != null){//如果ticket不为空
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            //将ticket与数据库中的ticket相关信息进行对比
            if(loginTicket == null || loginTicket.getExpird().before(new Date()) || loginTicket.getStatus() != 0){
                return true;//ticket是有效的
            }

            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);//将

        }
        return true;
    }

    /**
     * 渲染之前
     * handler处理完之后调用该函数
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null){
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    /**
     * 渲染结束时
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
