package com.jnu.dao;

import com.jnu.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by Damon on 2017/8/14.
 */
@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = "user_id, ticket,expired, status";
    String SELECT_FIELDS = "id, user_id, ticket,expired, status";

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId}, #{ticket}, #{expired}, #{status})"})
    int addTicket(LoginTicket ticket);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update", TABLE_NAME, "set status = #{status} where ticket = #{ticket}"})
    void updateStatus(@Param("ticket") String ticket,
                      @Param("status") int status);

}
