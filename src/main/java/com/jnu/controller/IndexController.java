package com.jnu.controller;

import com.jnu.model.*;
import com.jnu.service.CommentService;
import com.jnu.service.FollowService;
import com.jnu.service.QuestionService;
import com.jnu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpSession;
import javax.validation.executable.ValidateOnExecution;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Damon on 2017/8/13.
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;


    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET})
    public String userIndex(Model model,
                            @PathVariable("userId") int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        User user = userService.getUser(userId);
        ViewObject viewObject = new ViewObject();
        viewObject.set("user", user);
        viewObject.set("commentCount",commentService.getUserCommentCount(userId));
        viewObject.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        viewObject.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if(hostHolder.getUser() != null){
            viewObject.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        }else{
            viewObject.set("followed", false);
        }
        model.addAttribute("profileUser", viewObject);

        return "profile";
    }

    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET})
    public String index(Model model){
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    @RequestMapping(path = {"redirect/{code}"}, method = {RequestMethod.GET})
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession){
        httpSession.setAttribute("msg", "jump from redirect");
        RedirectView redirectView = new RedirectView("/", true);
        if(code == 301){
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return redirectView;
    }

    private List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for(Question question : questionList){
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

}
