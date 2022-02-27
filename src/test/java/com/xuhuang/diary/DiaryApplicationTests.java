package com.xuhuang.diary;

import com.xuhuang.diary.controllers.AppController;
import com.xuhuang.diary.controllers.api.v1.AuthRestController;
import com.xuhuang.diary.services.UserService;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiaryApplicationTests {

	@Autowired
	private AppController appController;
	@Autowired
	private AuthRestController authRestController;
	@Autowired
	private UserService userService;

	@Test
	void contextLoads() {
		Assertions.assertThat(appController).isNotNull();
		Assertions.assertThat(authRestController).isNotNull();
		Assertions.assertThat(userService).isNotNull();
	}

}
