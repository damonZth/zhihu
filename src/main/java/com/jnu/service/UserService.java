package com.jnu.service;

import com.jnu.dao.LoginTicketDAO;
import com.jnu.dao.UserDAO;
import com.jnu.model.LoginTicket;
import com.jnu.model.User;
import com.jnu.utils.ZhihuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by Damon on 2017/8/13.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;



    /**
     * 实现注册功能
     * @param username
     * @param password
     * @return
     */
    public Map<String, String> register(String username, String password){
        Map<String, String> map = new HashMap<>();
        if(StringUtils.isBlank(username)){//判断用户名是否为空
            map.put("msg", "用户名不能为空。");
            return map;
        }
        if(StringUtils.isBlank(password)){//判断密码是否为空
            map.put("msg", "密码不能为空。");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user != null){//判断用户名是否已经被注册
            map.put("msg", "用户名已经被注册.");
            return map;
        }
        //注册用户
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        //设置随机头像
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(ZhihuUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);
        //下发ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    /**
     * 登入
     * @param username
     * @param password
     * @return
     */
    public Map<String,String> login(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if(StringUtils.isBlank(username)){//判断用户名是否为空
            map.put("msg", "用户名不能为空。");
            return map;
        }
        if(StringUtils.isBlank(password)){//判断密码是否为空
            map.put("msg", "密码不能为空。");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user == null){//判断用户名是否已经被注册
            map.put("msg", "用户不存在。");
            return map;
        }
        if(! ZhihuUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码错误！");
            return map;
        }
        //下发ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    /**
     * 增加ticket
     * @param userId
     * @return
     */
    private String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600*24*7 + now.getTime());//设置过期时间
        loginTicket.setExpird(now);
        loginTicket.setStatus(0);//0 表示状态有效
        //随机生成ticket
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket, 1);
    }


    public User getUser(int id){
        return userDAO.selectById(id);
    }
}
