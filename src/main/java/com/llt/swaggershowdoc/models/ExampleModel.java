package com.llt.swaggershowdoc.models;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.llt.swaggershowdoc.util.ExampleModelSerializer;
import io.swagger.util.Json;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author LILONGTAO
 * @date 2019-07-10
 * @Description
 */
@JsonSerialize(using = ExampleModelSerializer.class)
public class ExampleModel  implements Map<String,Object> {

    private List list;

    private Map map;

    public ExampleModel(List list) {
        this.list = list;
    }

    public ExampleModel(Map map) {
        this.map = map;
    }


    public static void main(String[] args) {
        ExampleModel exampleModel;
        List<String> list = new ArrayList<>();
        list.add("str");
        exampleModel = new ExampleModel(list);
        System.out.println(JSON.toJSON(exampleModel));

        System.out.println(Json.pretty(exampleModel));

    }

    public String toPrettyString() {
        return list != null ? JSON.toJSONString(list, SerializerFeature.PrettyFormat) : JSON.toJSONString(map,SerializerFeature.PrettyFormat);
    }

    @Override
    public String toString() {
        return list != null ? JSON.toJSONString(list) : JSON.toJSONString(map);
    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Object> values() {
        return null;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return null;
    }
}
