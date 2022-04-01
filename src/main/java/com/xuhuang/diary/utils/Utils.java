package com.xuhuang.diary.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Utils {

    private Utils() {}

    /*
        Converts an Object to a JSON string
    */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
        Converts a date string in ISO format to Timestamp
    */
    public static Timestamp asTimestamp(String timeIsoStr) {
        return Timestamp.valueOf(
            LocalDateTime.parse(timeIsoStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

}
