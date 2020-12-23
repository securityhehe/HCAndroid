package frame.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtils {
    /**
     * 返回由对象的属性为 key,值为 map 的 value 的 Map 集合
     */
    public static Map<String, Object> getFieldValue(Object obj) {
        Map<String, Object> mapValue = new HashMap<>();
        Field[]             fields   = obj.getClass().getDeclaredFields();
        Field.setAccessible(fields, true);
        try {
            for (Field field : fields) {
                String name  = field.getName();
                Object value = field.get(obj);
                if (null != value) {
                    mapValue.put(name, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mapValue;
    }
}
