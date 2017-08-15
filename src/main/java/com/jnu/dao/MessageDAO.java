package com.jnu.dao;

import com.jnu.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;

import java.util.List;

/**
 * Created by Damon on 2017/8/15.
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = "from_id, to_id, content, created_date, has_read, conversation_id";
    String SELECT_FIELDS = "id, from_id, to_id, content, create_date, has_read, conversation_id";

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME,
            "where conversation_id = #{conversationId} order by desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversation") String conversation,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"select count(id) from", TABLE_NAME,
            "where has_read = 0 and to_id = #{userId} and conversationId = #{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId,
                                   @Param("conversationId") String conversationId);

    @Select({"select ", INSERT_FIELDS, " ,count(id) as id from ( select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by conversation_id  order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset, @Param("limit") int limit);

}
