package com.jnu.dao;

import com.jnu.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Damon on 2017/8/13.
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = "question";
    String INSERT_FIELDS = "title, content, created_date, user_id, comment_count";
    String SELECT_FIELDS = "id, title, content, created_date, user_id, comment_count";

    /**
     * 添加 问题 到 question表
     * @param question
     * @return
     */
    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{title}, #{content}, #{createdDate},#{userId}, #{commentCount})"})
    int addQuestion(Question question);


    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    /**
     * 根据id选择出问题
     * @param id
     * @return
     */
    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    Question selectById(int id);

    /**
     * 更新问题的commentCount
     * @param id
     * @param commentCount
     * @return
     */
    @Update({"update", TABLE_NAME, "set comment_count = #{commentCount} where id = #{id}"})
    int updateCommentCount(@Param("id") int id,
                           @Param("commentCount") int commentCount);
}
