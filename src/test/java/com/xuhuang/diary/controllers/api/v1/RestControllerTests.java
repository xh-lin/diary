package com.xuhuang.diary.controllers.api.v1;

import static com.xuhuang.diary.utils.Utils.asJsonString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public abstract class RestControllerTests {

    private static final String ERRORS = "$.errors";

    @Autowired
    private MockMvc mockMvc;

    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate, Object requestBody,
            HttpStatus httpStatus, boolean checkErrorsSize, String... errors) throws Exception {
        ResultActions resultActions = mockMvc.perform(
            request(httpMethod, urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
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

    protected void mockMvcTest(HttpMethod httpMethod, String urlTemplate, Object requestBody,
            HttpStatus httpStatus) throws Exception {
        mockMvcTest(httpMethod, urlTemplate, requestBody, httpStatus, false);
    }

}
