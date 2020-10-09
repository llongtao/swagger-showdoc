package top.aexp.swaggershowdoc.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import top.aexp.swaggershowdoc.util.ExampleModelSerializer;
import top.aexp.swaggershowdoc.util.Jackson;

import java.util.*;

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
        System.out.println(Jackson.toJSONString(exampleModel));

    }


    @Override
    public String toString() {
        return list != null ? Jackson.toJSONString(list) : Jackson.toJSONString(map);
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
