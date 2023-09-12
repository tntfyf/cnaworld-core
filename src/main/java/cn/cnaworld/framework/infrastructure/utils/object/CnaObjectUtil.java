package cn.cnaworld.framework.infrastructure.utils.object;

import org.apache.commons.lang3.ObjectUtils;

import java.time.temporal.Temporal;
import java.util.Date;

/**
 * @author Lucifer
 */
public class CnaObjectUtil extends ObjectUtils {

    public static boolean notObject(Object param){
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

    public static boolean notObjectClass(Class<?> clazz){
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
}
