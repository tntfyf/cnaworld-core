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
 * @date 2023/4/10
 * @since 1.0.3
 */
@Slf4j
public class CnaLogger {

    private static boolean initFlag;

    @Autowired
    private CnaworldLogProperties cnaworldLog;

    private static CnaworldLogProperties cnaworldLogProperties;

    @PostConstruct
    private void init() {
        cnaworldLogProperties=cnaworldLog;
        if (!initFlag) {
            initCache();
        }
        log.info("CnaLogUtil initialized");
    }

    private static volatile Map<String, LogLevel> localCachedMap = null;

    private void initCache(){
        initLogProperties();
    }

    /**
     * 初始化缓存
     * @author Lucifer
     * @date 2023/3/8
     * @since 1.0.0
     */
    private static synchronized void initLogProperties() {
        initFlag = true;
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

    private static synchronized void cacheMap(List<CnaworldLogProperties.LogProperties> logProperties) {
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
     * 日志分等级打印
     * @author Lucifer
     * @date 2023/3/19
     * @since 1.0.0
     */
    protected static void log(Logger log, LogLevel defaultLogLeave, String format, Object... arguments){
        if (log == null){
            return;
        }
        LogLevel logLeave = null;
        if (!initFlag) {
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
