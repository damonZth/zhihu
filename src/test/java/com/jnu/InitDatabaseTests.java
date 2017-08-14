package com.jnu;

import com.jnu.dao.QuestionDAO;
import com.jnu.dao.UserDAO;
import com.jnu.model.Question;
import com.jnu.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/init-schema.sql")
public class InitDatabaseTests {

	@Autowired
	UserDAO userDAO;

	@Autowired
	QuestionDAO questionDAO;

	@Test
	public void initDatabase() {
		Random random = new Random();
		for(int i = 0; i < 11; i++){
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("User%d", i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);

			user.setPassword("xx");
			userDAO.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*i);
			question.setCreatedDate(date);
			question.setUserId(i+1);
			question.setTitle(String.format("问题{%d}", i));
			question.setContent(String.format("暂时随便评论点什么咯---- %d", i));
			questionDAO.addQuestion(question);
		}
		Assert.assertEquals("xx", userDAO.selectById(1).getPassword());
		userDAO.deleteById(1);
		Assert.assertNull(userDAO.selectById(1));

		System.out.println(questionDAO.selectLatestQuestions(0,0,10));

	}

}
