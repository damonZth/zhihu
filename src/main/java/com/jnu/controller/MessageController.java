package com.jnu.controller;

import com.jnu.model.HostHolder;
import com.jnu.model.Message;
import com.jnu.model.User;
import com.jnu.model.ViewObject;
import com.jnu.service.MessageService;
import com.jnu.service.UserService;
import com.jnu.utils.ZhihuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

   @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
   public String getConversationList(Model model){
        try{
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            //System.out.println(1);
            for(Message message : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("conversation", message);
                //System.out.println(2);
                int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                //System.out.println(3);
                vo.set("unread", messageService.getConversationUnreadCount(localUserId, message.getConversationId()));
                //System.out.println(4);
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);

        }catch (Exception e){
            //System.out.println(7);
            logger.error("获取站内信失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId") String conversationId){
        System.out.println(conversationId);
       try{

            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message message : messageList){
                ViewObject viewObject = new ViewObject();
                viewObject.set("message", message);
                viewObject.set("user", userService.getUser(message.getFromId()));
                messageService.setMessageHasRead(conversationId, 1);
                messages.add(viewObject);
            }
            model.addAttribute("messages", messages);
       }catch (Exception e){
           logger.error("获取详情失败" + e.getMessage());
       }
       return "letterDetail";
    }

}
