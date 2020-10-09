package top.aexp.swaggershowdoc.util;

import io.swagger.models.Model;
import io.swagger.models.properties.*;

import java.util.*;

/**
 * @author LILONGTAO
 * @date 2019-07-10
 */
public class ExampleUtil {
    public static HashMap<String, Object> getBodyExample(Model model, Map<String, Model> definitions, Set<String> modelSet) {
        if (modelSet == null) {
            modelSet = new HashSet<>();
        }
        if (model == null) {
            return new HashMap<>();
        }
        Map<String, Property> properties = model.getProperties();
        if (properties == null) {
            return new HashMap<>();
        }

        HashMap<String, Object> resultExample = new HashMap<>(16);
        Set<String> tmpModelSet = new HashSet<>();

        for (String key : properties.keySet()) {
            Property property = properties.get(key);
            String type = property.getType();
            boolean isMap = property instanceof MapProperty;
            Object value;
            Map<Object, Object> valueMap = null;
            if (isMap) {
                valueMap = new HashMap<>(1);
                property = getNoMapProperty(valueMap, (MapProperty) property);
                type = property.getType();
            }
            Object example = property.getExample();
            if (property instanceof DateTimeProperty) {
                if (example == null) {
                    value = "yyyy-MM-dd HH:mm:ss";
                }else {
                    value = example.toString();
                }
            } else if (property instanceof DateProperty) {
                if (example == null) {
                    value = "yyyy-MM-dd";
                }else {
                    value = example.toString();
                }
            } else if (property instanceof BooleanProperty) {
                value = true;
            } else if (property instanceof ArrayProperty) {
                value = getArrayExample((ArrayProperty) property, definitions, modelSet);
            } else if (property instanceof RefProperty) {
                String title = ((RefProperty) property).getSimpleRef();
                if (modelSet.contains(title) && !tmpModelSet.contains(title)) {
                    value = title;
                } else {
                    tmpModelSet.add(title);
                    value = getRefValue(definitions, modelSet, title, example);

                }
            } else if (property instanceof BaseIntegerProperty) {
                value = 0;
            } else if (property instanceof DecimalProperty) {
                value = 0.00;
            } else {
                value = type;
            }
            if (isMap) {
                setMapValue(valueMap, value);
                value = valueMap;
            }
            resultExample.put(key, value);
        }
        return resultExample;
    }

    private static void setMapValue(Map valueMap, Object value) {
        Object aValue = valueMap.get("key");
        if (aValue instanceof Map) {
            setMapValue((Map) aValue, value);
        } else {
            valueMap.put("key", value);
        }
    }

    private static Property getNoMapProperty(Map<Object, Object> valueMap, MapProperty property) {

        Property additionalProperties = property.getAdditionalProperties();
        if (additionalProperties instanceof MapProperty) {
            HashMap<Object, Object> newMap = new HashMap<>(1);
            valueMap.put("key", newMap);
            return getNoMapProperty(newMap, (MapProperty) additionalProperties);
        } else {
            valueMap.put("key", "value");
            return additionalProperties;
        }
    }

    private static ArrayList getArrayExample(ArrayProperty arrayProperty, Map<String, Model> definitions, Set<String> modelSet) {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        Property items = arrayProperty.getItems();
        if (items instanceof RefProperty) {
            String simpleRef = ((RefProperty) items).getSimpleRef();
            Object example = items.getExample();
            if (modelSet.contains(simpleRef)) {
                objectArrayList.add(simpleRef);
            }else {
                objectArrayList.add(getRefValue(definitions, modelSet, simpleRef, example));
            }

        } else if (items instanceof ArrayProperty) {
            objectArrayList.add(getArrayExample((ArrayProperty) items, definitions, modelSet));
        } else {
            objectArrayList.add(items.getType());
        }
        return objectArrayList;
    }

    private static Object getRefValue(Map<String, Model> definitions, Set<String> modelSet, String simpleRef, Object example) {
        Object value;
        if ("LocalDate".equals(simpleRef)) {
            value = "yyyy-MM-dd";
        } else if ("LocalDateTime".equals(simpleRef)) {
            value = "yyyy-MM-dd HH:mm:ss";
        } else if ("LocalTime".equals(simpleRef)) {
            value = "HH:mm:ss";
        } else {
            modelSet.add(simpleRef);
            Model model1 = definitions.get(simpleRef);
            if (model1 == null) {
                value = example;
            } else {
                value = getBodyExample(model1, definitions, modelSet);
            }
        }
        return value;
    }


}
