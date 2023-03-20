package cn.cnaworld.framework.infrastructure.config;

import cn.cnaworld.framework.infrastructure.properties.log.CnaworldLogProperties;
import cn.cnaworld.framework.infrastructure.properties.systemconfig.SystemConfigProperties;
import cn.cnaworld.framework.infrastructure.utils.log.CnaLogUtil;
import cn.cnaworld.framework.infrastructure.utils.resources.CnaSysConfigUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动装配类
 * @author Lucifer
 * @date 2023/2/10
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties({CnaworldLogProperties.class,SystemConfigProperties.class})
@Import(value = {CnaLogUtil.class, CnaSysConfigUtil.class})
public class CnaCoreAutoConfig {}

