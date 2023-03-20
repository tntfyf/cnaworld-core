package cn.cnaworld.framework.infrastructure.properties.log;

import cn.cnaworld.framework.infrastructure.statics.enums.LogLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * cnaworld属性配置
 * @author Lucifer
 * @date 2023/1/30
 * @since 1.0
 */

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix="cnaworld.log")
public class CnaworldLogProperties {

    /**
     * 默认实现开关
     */
    private List<LogProperties> logProperties;

    @Setter
    @Getter
    @ToString
    public static class LogProperties {

        private String pathName;

        private LogLevel logLevel;

    }

}
