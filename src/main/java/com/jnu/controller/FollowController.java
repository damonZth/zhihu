package com.jnu.controller;

import com.jnu.async.EventModel;
import com.jnu.async.EventProducer;
import com.jnu.async.EventType;
import com.jnu.model.*;
import com.jnu.service.CommentService;
import com.jnu.service.FollowService;
import com.jnu.service.QuestionService;
import com.jnu.service.UserService;
import com.jnu.utils.ZhihuUtil;
import com.sun.xml.internal.stream.events.EntityReferenceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Damon on 2017/8/17.
 */
@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;

    /**
     * 关注用户
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser() == null){
            return ZhihuUtil.getJSONString(999);
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityOwnerId(userId));
        //返回用户的关注人数
        return ZhihuUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));

    }

    /**
     * 取消关注用户
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser() == null){
            return ZhihuUtil.getJSONString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        //返回用户的关注人数
        return ZhihuUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));

    }

    /**
     * 关注问题
     * @param questionId
     * @return
     */
    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.GET})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser() == null){
            return ZhihuUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if(question == null){
            return ZhihuUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(question.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return ZhihuUtil.getJSONString(ret ? 0 : 1, info);
    }

    /**
     * 取消关注问题
     * @param questionId
     * @return
     */
    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.GET})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser() == null){
            return ZhihuUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if(question == null){
            return ZhihuUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(question.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return ZhihuUtil.getJSONString(ret ? 0 : 1, info);
    }

    /**
     * 页面显示关注信息
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{userId}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("userId") int userId){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if(hostHolder.getUser() !=null){
            model.addAttribute("followers", getUserInfo(hostHolder.getUser().getId(), followerIds));
        }else{
            model.addAttribute("followers", getUserInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    /**
     * 页面显示关注者信息
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{userId}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("userId") int userId){
        List<Integer> followerIds = followService.getFollowees(userId,EntityType.ENTITY_USER, 0, 10);
        if(hostHolder.getUser() !=null){
            model.addAttribute("followees", getUserInfo(hostHolder.getUser().getId(), followerIds));
        }else{
            model.addAttribute("followees", getUserInfo(0, followerIds));
        }
        model.addAttribute("followeeCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }


    private List<ViewObject> getUserInfo(int localUserId, List<Integer> userIds){
        List<ViewObject> userInfos = new ArrayList<>();
        for(Integer userId : userIds){
            User user = userService.getUser(userId);
            if(user == null){
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(userId));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
            if(localUserId != 0){
                vo.set("follower", followService.isFollower(localUserId, EntityType.ENTITY_USER, userId));
            }else{
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
