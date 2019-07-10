package com.llt.swaggershowdoc.util;

import io.swagger.models.Model;
import io.swagger.models.properties.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LILONGTAO
 * @date 2019-07-10
 * @Description
 */
public class ExampleUtil {
    public static HashMap<String, Object> getBodyExample(Model model, Map<String, Model> definitions) {
        Map<String, Property> properties = model.getProperties();
        HashMap<String, Object> resultExample = new HashMap<>(16);
        for (String key : properties.keySet()) {

            Property property = properties.get(key);
            String type = property.getType();
            if (property instanceof DateTimeProperty) {
                resultExample.put(key, "yyyy-MM-dd HH:mm:ss");
            } else if (property instanceof DateProperty) {
                resultExample.put(key, "yyyy-MM-dd");
            } else if (property instanceof BooleanProperty) {
                resultExample.put(key, true);
            } else if (property instanceof ArrayProperty) {
                Property items = ((ArrayProperty) property).getItems();
                if (items instanceof RefProperty) {
                    String simpleRef = ((RefProperty) items).getSimpleRef();
                    Model model1 = definitions.get(simpleRef);
                    ArrayList<Object> objectArrayList = new ArrayList<>();
                    objectArrayList.add(getBodyExample(model1, definitions));
                    resultExample.put(key, objectArrayList);
                } else {
                    ArrayList<Object> objectArrayList = new ArrayList<>();
                    objectArrayList.add(items.getType());
                    resultExample.put(key, objectArrayList);
                }
            } else if (property instanceof MapProperty) {
                Map map = new HashMap(2);
                map.put("key1", "value1");
                map.put("key2", "value2");
                resultExample.put(key, map);
            } else if (property instanceof RefProperty) {
                String simpleRef = ((RefProperty) property).getSimpleRef();
                Model model1 = definitions.get(simpleRef);
                resultExample.put(key, getBodyExample(model1, definitions));
            } else if (property instanceof BaseIntegerProperty) {
                resultExample.put(key, 0);
            } else if (property instanceof DecimalProperty) {
                resultExample.put(key, 0.00);
            } else {
                resultExample.put(key, type);
            }


        }
        return resultExample;
    }
}
