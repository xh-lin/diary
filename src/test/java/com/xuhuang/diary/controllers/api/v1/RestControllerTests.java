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

    protected static final String DATA_JPEXP = "$.data";
    protected static final String ERROR_JPEXP = "$.error";
    protected static final String ERRORS_JPEXP = "$.errors";
    protected static final String MESSAGE_JPEXP = "$.message";

    protected static final Long MOCK_USER_ID = 1L;
    protected static final String MOCK_USERNAME = "mockUser";
    protected static final String MOCK_EMAIL = "mock@user.com";
    protected static final String MOCK_PASSWORD = "Qwerty123.";
    protected static final String MOCK_PASSWORD_ENCRYPTED = new BCryptPasswordEncoder().encode(MOCK_PASSWORD);
    protected static final UserRole MOCK_USER_ROLE = UserRole.USER;

    @Autowired
    private MockMvc mockMvc;

    protected static User mockUser() {
        User user = new User(
            MOCK_USERNAME,
            MOCK_EMAIL,
            MOCK_PASSWORD_ENCRYPTED,
            MOCK_USER_ROLE);
        user.setId(MOCK_USER_ID);
        return user;
    }

    protected ResultActions mockMvcPerform(HttpMethod httpMethod, String urlTemplate,
            MultiValueMap<String, String> requestParams, Object requestBody, User user,
            HttpStatus httpStatus) throws Exception {
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

        return mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().is(httpStatus.value()));
    }

    /*
        httpMethod, urlTemplate, requestParams, user, httpStatus
    */
    protected ResultActions mockMvcPerform(HttpMethod httpMethod, String urlTemplate,
            MultiValueMap<String, String> requestParams, User user,
            HttpStatus httpStatus) throws Exception {
        return mockMvcPerform(
            httpMethod, urlTemplate,
            requestParams, null, user,
            httpStatus);
    }

    /*
        httpMethod, urlTemplate, user, httpStatus
    */
    protected ResultActions mockMvcPerform(HttpMethod httpMethod, String urlTemplate,
            User user,
            HttpStatus httpStatus) throws Exception {
        return mockMvcPerform(
            httpMethod, urlTemplate,
            null, null, user,
            httpStatus);
    }

    /*
        httpMethod, urlTemplate, requestBody, httpStatus
    */
    protected ResultActions mockMvcPerform(HttpMethod httpMethod, String urlTemplate,
            Object requestBody,
            HttpStatus httpStatus) throws Exception {
        return mockMvcPerform(
            httpMethod, urlTemplate,
            null, requestBody, null,
            httpStatus);
    }

    protected ResultActions expectValue(ResultActions resultActions,
            String jsonPathExpression, Object expectedValue) throws Exception {
        return resultActions.andExpect(jsonPath(jsonPathExpression).value(expectedValue));
    }

    protected ResultActions expectArray(ResultActions resultActions,
            Integer size, boolean exactSize,
            String jsonPathExpression, Object... items) throws Exception {
        resultActions.andExpect(jsonPath(jsonPathExpression).isArray());
        resultActions.andExpect(jsonPath(jsonPathExpression, hasItems(items)));

        if (exactSize) {
            resultActions.andExpect(jsonPath(jsonPathExpression, hasSize(items.length)));
        } else if (size != null) {
            resultActions.andExpect(jsonPath(jsonPathExpression, hasSize(size)));
        }

        return resultActions;
    }

    /*
        resultActions, jsonPathExpression, items
    */
    protected ResultActions expectArray(ResultActions resultActions,
            String jsonPathExpression,  Object... items) throws Exception {
        return expectArray(
            resultActions,
            null, true,
            jsonPathExpression, items);
    }

}
