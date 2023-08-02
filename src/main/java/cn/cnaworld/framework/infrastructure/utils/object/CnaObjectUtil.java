package cn.cnaworld.framework.infrastructure.utils.object;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

/**
 * @author Lucifer
 */
public class CnaObjectUtil extends ObjectUtils {

    public static boolean isObject(Object param){
        if (param instanceof Integer) {
            return false;
        } else if (param instanceof String) {
            return false;
        } else if (param instanceof Double) {
            return false;
        } else if (param instanceof Float) {
            return false;
        } else if (param instanceof Long) {
            return false;
        } else if (param instanceof Boolean) {
            return false;
        } else {
            return !(param instanceof Date);
        }
    }

}
