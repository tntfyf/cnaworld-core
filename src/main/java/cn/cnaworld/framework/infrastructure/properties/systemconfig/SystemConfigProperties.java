package cn.cnaworld.framework.infrastructure.properties.systemconfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "cnaworld")
public class SystemConfigProperties {

	private Map<String,Object> systemConfig;

}
