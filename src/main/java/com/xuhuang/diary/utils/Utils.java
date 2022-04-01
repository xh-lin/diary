package com.xuhuang.diary.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Utils {

    private Utils() {}

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
