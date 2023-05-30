package cn.cnaworld.framework.infrastructure.utils.log;

import cn.cnaworld.framework.infrastructure.statics.enums.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * @author Lucifer
 * @date 2023/3/19
 * @since 1.0.0
 */
@Slf4j
public class CnaLogUtil {

    /**
     * 打印trace级别日志
     */
    public static void trace(Logger log, String format, Object... arguments){
        CnaLogger.log(log,LogLevel.TRACE,format,arguments);
    }

    /**
     * 打印debug级别日志
     */
    public static void debug(Logger log, String format, Object... arguments){
        CnaLogger.log(log,LogLevel.DEBUG,format,arguments);
    }

    /**
     * 打印info级别日志
     */
    public static void info(Logger log, String format, Object... arguments){
        CnaLogger.log(log,LogLevel.INFO,format,arguments);
    }

    /**
     * 打印warn级别日志
     */
    public static void warn(Logger log, String format, Object... arguments){
        CnaLogger.log(log,LogLevel.WARN,format,arguments);
    }

    /**
     * 打印error级别日志
     */
    public static void error(Logger log, String format, Object... arguments){
        CnaLogger.log(log,LogLevel.ERROR,format,arguments);
    }

}
