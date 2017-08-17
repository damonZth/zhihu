package com.jnu.service;

import com.jnu.dao.CommentDAO;
import com.jnu.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by Damon on 2017/8/15.
 */
@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentDAO commentDAO;
    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentsByentity(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId, entityType);
    }

    public int addComment(Comment comment){
        //过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public int getUserCommentCount(int userId){
        return commentDAO.getUserCommentCount(userId);
    }

//    public boolean deleteComment(int commentId){
//        return commentDAO.updateStatus(commentId, 1) > 0;
//    }

    /**
     * 通过设置comment的状态，使用标记留言被删除
     * @param entityId
     * @param entityType
     */
    public void deleteComment(int entityId, int entityType){
        commentDAO.updateStatus(entityId, entityType, 1);
    }

    public Comment getCommentById(int id){
        return commentDAO.getCommentById(id);
    }
}
