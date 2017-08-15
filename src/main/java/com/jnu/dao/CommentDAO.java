package com.jnu.dao;

import com.jnu.model.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Damon on 2017/8/15.
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = "content, user_id, entity_id, entity_type, created_date, status";
    String SELECT_FIELDS = "id, content, user_id, entity_id, entity_type, created_date, status";

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{content}, #{userId}, #{entityId}, #{entityType}, #{createdDate}, #{status})"})
    int addComment(Comment comment);

    @Update({"update", TABLE_NAME, "set status = #{status} where entity_id = #{entityId} and entity_type = #{entityType}"})
    void updateStatus(@Param("entityId") int entityId,
                      @Param("entityType") int entityType,
                      @Param("status") int status);

    @Select({"select count(id) from", TABLE_NAME, "where entity_id = #{entityId} and entity_type = #{entityType} "})
    int getCommentCount(@Param("entityId") int entityId,
                        @Param("entityType") int entityType);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where entity_id = #{entityId} and entity_type = #{entityType} order by id desc"})
    List<Comment> selectByEntity(@Param("entityId") int entityId,
                                 @Param("entityType") int entityType);
}
