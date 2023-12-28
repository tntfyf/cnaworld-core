package cn.cnaworld.framework.infrastructure.utils.object;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.temporal.Temporal;
import java.util.Date;

/**
 * @author Lucifer
 */
public class CnaCheckUtil {

    public static boolean isNotObject(Object param){
        if (param instanceof Number) {
            return true;
        } else if (param instanceof String) {
            return true;
        } else if (param instanceof Temporal) {
            return true;
        } else if (param instanceof Boolean) {
            return true;
        } else {
            return param instanceof Date;
        }
    }

    public static boolean isNotObjectClass(Class<?> clazz){
        if (Number.class.isAssignableFrom(clazz)) {
            return true;
        } else if (String.class.isAssignableFrom(clazz)) {
            return true;
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            return true;
        } else if (Temporal.class.isAssignableFrom(clazz)) {
            return true;
        }else if (clazz.isPrimitive()) {
            return true;
        }else {
            return Date.class.isAssignableFrom(clazz);
        }
    }

    public static <T> boolean isNotNull(T t){
        if (t instanceof String){
            return StringUtils.isNotBlank((String) t);
        }else {
            return ObjectUtils.isNotEmpty(t);
        }
    }

    public static <T> boolean isNull(T t){
        return !isNotNull(t);
    }
}
