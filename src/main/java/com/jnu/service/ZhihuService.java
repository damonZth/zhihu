package com.jnu.service;

import org.springframework.stereotype.Service;

/**
 * Created by Damon on 2017/8/14.
 */
@Service
public class ZhihuService {
    public String getMessage(int userId){
        return "Hello Message" + String.valueOf(userId);
    }
}
