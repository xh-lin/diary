package com.xuhuang.diary.controllers.api.v1;

import static com.xuhuang.diary.utils.Utils.asJsonString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.xuhuang.diary.models.User;
import com.xuhuang.diary.models.UserRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;

public abstract class RestControllerTests {

    private static final String ERROR = "$.error";
    private static final String ERRORS = "$.errors";
    protected static final String MOCK_USERNAME = "mockUser";
    protected static final String MOCK_EMAIL = "mock@user.com";
    protected static final String MOCK_PASSWORD = "Qwerty123.";
    protected static final User mockUser = new User(
        MOCK_USERNAME,
        MOCK_EMAIL,
        new BCryptPasswordEncoder().encode(MOCK_PASSWORD),
        UserRole.USER);

    @Autowired
    private MockMvc mockMvc;

    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            MultiValueMap<String, String> requestParams, Object requestBody, User user,
            HttpStatus httpStatus, String error, boolean checkErrorsSize, String... errors) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = request(httpMethod, urlTemplate);

        if (requestParams != null) {
            requestBuilder.params(requestParams);
        }

        if (requestBody != null) {
            requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody));
        }

        if (user != null) {
            requestBuilder.with(user(user));
        }

        ResultActions resultActions = mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().is(httpStatus.value()));

        if (error != null) {
            resultActions.andExpect(jsonPath(ERROR).value(error));
        }

        if (errors.length > 0) {
            resultActions.andExpect(jsonPath(ERRORS).isArray());
            resultActions.andExpect(jsonPath(ERRORS, hasItems(errors)));
            if (checkErrorsSize) {
                resultActions.andExpect(jsonPath(ERRORS, hasSize(errors.length)));
            }
        }
    }

    /*
        httpMethod, urlTemplate, requestBody, httpStatus, errors
    */
    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            Object requestBody,
            HttpStatus httpStatus, boolean checkErrorsSize, String... errors) throws Exception {
        mockMvcTest(
            httpMethod, urlTemplate,
            null, requestBody, null,
            httpStatus, null, checkErrorsSize, errors);
    }

    /*
        httpMethod, urlTemplate, requestBody, httpStatus
    */
    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            Object requestBody,
            HttpStatus httpStatus) throws Exception {
        mockMvcTest(
            httpMethod, urlTemplate,
            null, requestBody, null,
            httpStatus, null, false);
    }

    /*
        httpMethod, urlTemplate, requestParams, user, httpStatus, error
    */
    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            MultiValueMap<String, String> requestParams, User user,
            HttpStatus httpStatus, String error) throws Exception {
        mockMvcTest(
            httpMethod, urlTemplate,
            requestParams, null, user,
            httpStatus, error, false);
    }

    /*
        httpMethod, urlTemplate, requestParams, user, httpStatus
    */
    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            MultiValueMap<String, String> requestParams, User user,
            HttpStatus httpStatus) throws Exception {
        mockMvcTest(
            httpMethod, urlTemplate,
            requestParams, null, user,
            httpStatus, null, false);
    }

}
