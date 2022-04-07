package com.xuhuang.diary;

import com.xuhuang.diary.controllers.AppController;
import com.xuhuang.diary.controllers.AuthController;
import com.xuhuang.diary.controllers.DiaryController;
import com.xuhuang.diary.controllers.ErrorController;
import com.xuhuang.diary.controllers.api.v1.AuthRestController;
import com.xuhuang.diary.controllers.api.v1.DiaryRestController;
import com.xuhuang.diary.services.DiaryService;
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
	private AuthController authController;
	@Autowired
	private DiaryController diaryController;
	@Autowired
	private ErrorController errorController;

	@Autowired
	private AuthRestController authRestController;
	@Autowired
	private DiaryRestController diaryRestController;

	@Autowired
	private UserService userService;
	@Autowired
	private DiaryService diaryService;

	@Test
	void contextLoads() {
		Assertions.assertThat(appController).isNotNull();
		Assertions.assertThat(authController).isNotNull();
		Assertions.assertThat(diaryController).isNotNull();
		Assertions.assertThat(errorController).isNotNull();

		Assertions.assertThat(authRestController).isNotNull();
		Assertions.assertThat(diaryRestController).isNotNull();

		Assertions.assertThat(userService).isNotNull();
		Assertions.assertThat(diaryService).isNotNull();
	}

}
