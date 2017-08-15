package com.jnu.controller;

import com.jnu.model.Comment;
import com.jnu.model.EntityType;
import com.jnu.model.HostHolder;
import com.jnu.service.CommentService;
import com.jnu.service.QuestionService;
import com.jnu.utils.ZhihuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by Damon on 2017/8/15.
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;


    /**
     * 增加评论
     * @param questionId
     * @param content
     * @return
     */
    @RequestMapping(path = {"addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            //System.out.println(1);
            comment.setContent(content);
            if (hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(ZhihuUtil.ANONYMOUS_USERID);
            }
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            //System.out.println(2);
            commentService.addComment(comment);

            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            System.out.println(count);
            questionService.updateCommentCount(comment.getEntityId(), count);
            System.out.println(999);
        }catch (Exception e){
            logger.error("添加评论失败" + e.getMessage());
        }
        //System.out.println(3);
        return "redirect:/question/" + questionId;
    }
}
