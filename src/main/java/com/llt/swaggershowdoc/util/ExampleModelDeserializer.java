package com.llt.swaggershowdoc.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.llt.swaggershowdoc.models.ExampleModel;

import java.io.IOException;

/**
 * @author LILONGTAO
 * @date 2019-07-10
 * @Description
 */
public class ExampleModelDeserializer extends JsonDeserializer<ExampleModel> {
    @Override
    public ExampleModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return null;
    }
}
