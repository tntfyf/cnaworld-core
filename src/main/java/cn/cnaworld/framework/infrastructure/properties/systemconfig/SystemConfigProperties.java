package cn.cnaworld.framework.infrastructure.properties.systemconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 自定义系统配置
 * @author Lucifer
 * @date 2023/3/10
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "cnaworld.system-config")
public class SystemConfigProperties {

	private Map<String,Object> configName;

}
