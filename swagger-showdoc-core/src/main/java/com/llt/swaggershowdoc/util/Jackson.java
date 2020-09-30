package com.llt.swaggershowdoc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.io.IOException;

/**
 * @author LILONGTAO
 * @date 2020-09-30
 */
public class Jackson {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJSONString(Object o) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    public static <T> T parseObject(String str, Class<T> clz) {
        try {
            return objectMapper.readValue(str, clz);
        } catch (IOException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }
}
