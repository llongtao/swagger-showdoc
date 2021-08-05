package top.aexp.swaggershowdoc.util;

import java.lang.reflect.Field;

/**
 * @author lilongtao 2021/8/5
 */
public class BeanUtils {
    public static String getProperty(Object item, String field) {
        try {
            Field declaredField = item.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            Object o = declaredField.get(item);
            if (o != null) {
                return o.toString();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
