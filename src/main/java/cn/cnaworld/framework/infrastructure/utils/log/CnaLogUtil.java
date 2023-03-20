package cn.cnaworld.framework.infrastructure.utils.log;

import cn.cnaworld.framework.infrastructure.properties.log.CnaworldLogProperties;
import cn.cnaworld.framework.infrastructure.statics.enums.LogLevel;
import cn.cnaworld.framework.infrastructure.utils.resources.CnaSysConfigUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Lucifer
 * @date 2023/3/19
 * @since 1.0.0
 */
@Slf4j
public class CnaLogUtil {

    @Autowired
    private CnaworldLogProperties cnaworldLog;

    private static CnaworldLogProperties cnaworldLogProperties;

    @PostConstruct
    private void init() {
        cnaworldLogProperties=cnaworldLog;
        initCache();
        log.info("CnaLogUtil initialized");
    }

    private static Map<String, LogLevel> localCachedMap = null;

    private void initCache(){
        initLogProperties();
    }

    /**
     * 初始化缓存
     * @author Lucifer
     * @date 2023/3/8
     * @since 1.0.0
     */
    private static void initLogProperties() {
        List<CnaworldLogProperties.LogProperties> logProperties;
        if (cnaworldLogProperties != null && ObjectUtils.isEmpty(localCachedMap)) {
            logProperties = cnaworldLogProperties.getLogProperties();
            cacheMap(logProperties);
        }else if (ObjectUtils.isEmpty(localCachedMap)){
            try{
                logProperties = CnaSysConfigUtil.getApplicationConfigByFullName("cnaworld.log.log-properties");
                if (logProperties != null) {
                    logProperties = JSON.parseObject(JSON.toJSONString(logProperties), new TypeReference<List<CnaworldLogProperties.LogProperties>>() {});
                    cacheMap(logProperties);
                }
            }catch (Exception e) {
                log.error("cnaworld log 获取配置信息失败",e);
            }
        }
    }

    private static void cacheMap(List<CnaworldLogProperties.LogProperties> logProperties) {
        localCachedMap = new TreeMap<>();
        if (ObjectUtils.isNotEmpty(logProperties)) {
            for (CnaworldLogProperties.LogProperties logProperty : logProperties) {
                LogLevel logLevel = logProperty.getLogLevel();
                if (logLevel==null) {
                    logLevel=LogLevel.DEBUG;
                }
                localCachedMap.put(logProperty.getPathName(),logLevel);
                log.info("cnaworld log 包路径:{} 日志等级调整为 : {} ",logProperty.getPathName() , logLevel);
            }
        }
    }

    /**
     * 打印trace级别日志
     */
    public static void trace(Logger log, String format, Object... arguments){
        log(log,LogLevel.TRACE,format,arguments);
    }

    /**
     * 打印debug级别日志
     */
    public static void debug(Logger log, String format, Object... arguments){
        log(log,LogLevel.DEBUG,format,arguments);
    }

    /**
     * 打印info级别日志
     */
    public static void info(Logger log, String format, Object... arguments){
        log(log,LogLevel.INFO,format,arguments);
    }

    /**
     * 打印warn级别日志
     */
    public static void warn(Logger log, String format, Object... arguments){
        log(log,LogLevel.WARN,format,arguments);
    }

    /**
     * 打印error级别日志
     */
    public static void error(Logger log, String format, Object... arguments){
        log(log,LogLevel.ERROR,format,arguments);
    }

    /**
     * 日志分等级打印
     * @author Lucifer
     * @date 2023/3/19
     * @since 1.0.0
     */
    private static void log(Logger log, LogLevel defaultLogLeave, String format, Object... arguments){
        LogLevel logLeave = null;
        if (ObjectUtils.isEmpty(localCachedMap) && cnaworldLogProperties ==null) {
            initLogProperties();
        }
        boolean updateFlag=false;
        if (ObjectUtils.isNotEmpty(localCachedMap)) {
            for (Map.Entry<String, LogLevel> entry : localCachedMap.entrySet()) {
                if (log.getName().startsWith(entry.getKey())) {
                    logLeave=localCachedMap.get(entry.getKey());
                    updateFlag = true;
                }
            }
        }
        if(!updateFlag) {
            logLeave=defaultLogLeave;
        }
        if(logLeave==null) {
            logLeave=LogLevel.DEBUG;
        }
        printLog(log , logLeave, format, arguments);
    }

    private static void printLog(Logger log,LogLevel logLeave, String format, Object... arguments) {
        switch (logLeave) {
            case TRACE:
                if (log.isTraceEnabled()) {
                    log.trace(format, arguments);
                }
                break;
            case DEBUG:
                if (log.isDebugEnabled()) {
                    log.debug(format, arguments);
                }
                    break;
            case INFO:
                if (log.isInfoEnabled()) {
                    log.info(format, arguments);
                }
                break;
            case WARN:
                if (log.isWarnEnabled()) {
                    log.warn(format, arguments);
                }
                break;
            case ERROR:
                if (log.isErrorEnabled()) {
                    log.error(format, arguments);
                }
                break;
            default:
                if (log.isDebugEnabled()) {
                    log.debug(format, arguments);
                }
                break;
        }
    }
}
