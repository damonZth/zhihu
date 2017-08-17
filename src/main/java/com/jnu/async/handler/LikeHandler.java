package com.jnu.async.handler;

import com.jnu.async.EventHandler;
import com.jnu.async.EventModel;
import com.jnu.async.EventType;
import com.jnu.model.Message;
import com.jnu.model.User;
import com.jnu.service.MessageService;
import com.jnu.service.UserService;
import com.jnu.utils.ZhihuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Damon on 2017/8/16.
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(ZhihuUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        System.out.println(eventModel.getExts("questionId"));
        message.setContent("用户" + user.getName() + "赞了你的回答, http://127.0.0.1:8080/question/" + eventModel.getExts("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
