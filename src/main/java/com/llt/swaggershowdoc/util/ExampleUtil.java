package com.llt.swaggershowdoc.util;

import io.swagger.models.Model;
import io.swagger.models.properties.*;

import java.util.*;

/**
 * @author LILONGTAO
 * @date 2019-07-10
 * @Description
 */
public class ExampleUtil {
    public static HashMap<String, Object> getBodyExample(Model model, Map<String, Model> definitions, Set<String> modelSet) {
        if (modelSet == null) {
            modelSet = new HashSet<>();
        }
        Map<String, Property> properties = model.getProperties();
        HashMap<String, Object> resultExample = new HashMap<>(16);
        for (String key : properties.keySet()) {
            Property property = properties.get(key);
            String type = property.getType();
            if (property instanceof DateTimeProperty) {
                resultExample.put(key, "datetime");
            } else if (property instanceof DateProperty) {
                resultExample.put(key, "date");
            } else if (property instanceof BooleanProperty) {
                resultExample.put(key, true);
            } else if (property instanceof ArrayProperty) {
                resultExample.put(key, getArrayExample((ArrayProperty) property, definitions,modelSet));
            } else if (property instanceof MapProperty) {
                Map map = new HashMap(2);
                map.put("key1", "value1");
                map.put("key2", "value2");
                resultExample.put(key, map);
            } else if (property instanceof RefProperty) {
                String title = property.getTitle();
                if (modelSet.contains(title)) {
                    resultExample.put(key, title);
                } else {
                    modelSet.add(title);
                    String simpleRef = ((RefProperty) property).getSimpleRef();
                    Model model1 = definitions.get(simpleRef);
                    resultExample.put(key, getBodyExample(model1, definitions, modelSet));
                }
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

    private static ArrayList getArrayExample(ArrayProperty arrayProperty, Map<String, Model> definitions, Set<String> modelSet) {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        Property items = arrayProperty.getItems();
        if (items instanceof RefProperty) {
            String simpleRef = ((RefProperty) items).getSimpleRef();
            Model model = definitions.get(simpleRef);
            objectArrayList.add(getBodyExample(model, definitions,modelSet));
        } else if (items instanceof ArrayProperty) {
            objectArrayList.add(getArrayExample((ArrayProperty) items, definitions,modelSet));
        } else {
            objectArrayList.add(items.getType());
        }
        return objectArrayList;
    }
}
