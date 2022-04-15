package com.xuhuang.diary;

import com.xuhuang.diary.controllers.api.v1.AuthRestController;
import com.xuhuang.diary.controllers.api.v1.DiaryRestController;
import com.xuhuang.diary.controllers.api.v1.TagRestController;
import com.xuhuang.diary.controllers.view.v1.AppController;
import com.xuhuang.diary.controllers.view.v1.AuthController;
import com.xuhuang.diary.controllers.view.v1.DiaryController;
import com.xuhuang.diary.controllers.view.v1.ErrorController;
import com.xuhuang.diary.services.BookService;
import com.xuhuang.diary.services.RecordService;
import com.xuhuang.diary.services.TagService;
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
    private TagRestController tagRestController;

	@Autowired
	private UserService userService;
	@Autowired
	private BookService bookService;
	@Autowired
	private RecordService recordService;
    @Autowired
    private TagService tagService;

	@Test
	void contextLoads() {
		Assertions.assertThat(appController).isNotNull();
		Assertions.assertThat(authController).isNotNull();
		Assertions.assertThat(diaryController).isNotNull();
		Assertions.assertThat(errorController).isNotNull();

		Assertions.assertThat(authRestController).isNotNull();
		Assertions.assertThat(diaryRestController).isNotNull();
		Assertions.assertThat(tagRestController).isNotNull();

		Assertions.assertThat(userService).isNotNull();
		Assertions.assertThat(bookService).isNotNull();
		Assertions.assertThat(recordService).isNotNull();
		Assertions.assertThat(tagService).isNotNull();
	}

}
