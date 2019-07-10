package com.llt.swaggershowdoc.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.llt.swaggershowdoc.models.ExampleModel;

import java.io.IOException;

/**
 * @author LILONGTAO
 * @date 2019-07-10
 * @Description
 */
public class ExampleModelSerializer extends JsonSerializer<ExampleModel> {

    @Override
    public void serialize(ExampleModel value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeRaw(value.toPrettyString());
    }
}
