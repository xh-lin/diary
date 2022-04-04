package com.xuhuang.diary.controllers.api.v1;

import static com.xuhuang.diary.utils.Utils.asJsonString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
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
            MultiValueMap<String, String> requestParams, Object requestBody,
            HttpStatus httpStatus, boolean checkErrorsSize, String... errors) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = request(httpMethod, urlTemplate);

        if (requestParams != null) {
            requestBuilder.params(requestParams);
        }

        if (requestBody != null) {
            requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody));
        }

        ResultActions resultActions = mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().is(httpStatus.value()));

        if (errors.length > 0) {
            resultActions.andExpect(jsonPath(ERRORS).isArray());
            resultActions.andExpect(jsonPath(ERRORS, hasItems(errors)));
            if (checkErrorsSize) {
                resultActions.andExpect(jsonPath(ERRORS, hasSize(errors.length)));
            }
        }
    }

    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            MultiValueMap<String, String> requestParams,
            HttpStatus httpStatus, boolean checkErrorsSize, String... errors) throws Exception {
        mockMvcTest(httpMethod, urlTemplate, requestParams, null, httpStatus, checkErrorsSize, errors);
    }

    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            Object requestBody,
            HttpStatus httpStatus, boolean checkErrorsSize, String... errors) throws Exception {
        mockMvcTest(httpMethod, urlTemplate, null, requestBody, httpStatus, checkErrorsSize, errors);
    }

    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            MultiValueMap<String, String> requestParams,
            HttpStatus httpStatus) throws Exception {
        mockMvcTest(httpMethod, urlTemplate, requestParams, null, httpStatus, false);
    }

    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate,
            Object requestBody,
            HttpStatus httpStatus) throws Exception {
        mockMvcTest(httpMethod, urlTemplate, null, requestBody, httpStatus, false);
    }

}
