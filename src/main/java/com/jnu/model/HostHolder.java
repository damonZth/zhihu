package com.jnu.model;

import org.springframework.stereotype.Component;

/**
 * Created by Damon on 2017/8/14.
 */
@Component
public class HostHolder {

    //为每一条线程都分配一个User对象
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public User getUser(){
        return userThreadLocal.get();
    }

    public void setUser(User user){
        userThreadLocal.set(user);
    }

    public void clear(){
        userThreadLocal.remove();
    }
}
