package com.jnu.dao;

import com.jnu.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Created by Damon on 2017/8/13.
 */
@Mapper
public interface UserDAO {

    String TABLE_NAME = "user";
    String INSERT_FIELDS = "name, password, salt, head_url";
    String SELECT_FIELDS = "id, name, password, salt, head_url";

    /**
     * 向数据库中增加 user 用户
     * @param user
     * @return
     */
    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{name}, #{password}, #{salt}, #{headUrl})"})
    int addUser(User user);

    /**
     * 根据id选择出user
     * @param id
     * @return
     */
    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    User selectById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where name = #{name}"})
    User selectByName(String name);

    /**
     * 根据id更新password
     * @param user
     */
    @Update({"update", TABLE_NAME, "set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    /**
     * 根据id来删除user
     * @param id
     */
    @Delete({"delete from", TABLE_NAME, "where id = #{id}"})
    void deleteById(int id);

}
