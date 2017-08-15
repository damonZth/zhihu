package com.jnu.controller;

import com.jnu.model.HostHolder;
import com.jnu.model.Message;
import com.jnu.model.User;
import com.jnu.service.MessageService;
import com.jnu.service.UserService;
import com.jnu.utils.ZhihuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


/**
 * Created by Damon on 2017/8/15.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;


    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try{
            if(hostHolder.getUser() == null){
                return ZhihuUtil.getJSONString(999,"未登入");
            }
            User user = userService.selectByName(toName);
            if(user == null){
                return ZhihuUtil.getJSONString(1, "用户不存在");
            }
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            messageService.addMessage(message);
            return ZhihuUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("发送失败" + e.getMessage());
            return ZhihuUtil.getJSONString(1, "发送失败");
        }
    }



}
