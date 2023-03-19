package cn.cnaworld.framework.infrastructure.utils.resources;

import cn.cnaworld.framework.infrastructure.properties.systemconfig.SystemConfigProperties;
import cn.cnaworld.framework.infrastructure.utils.CnaLogUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取系统配置及资源信息
 * @author Lucifer
 * @date 2023/3/9
 * @since 1.0.0
 */
@Setter
@Slf4j
public class CnaSysConfigUtil {

	@Autowired(required = false)
    private Environment environmentProperties;

	private static Environment environment;

	@Autowired(required = false)
	private SystemConfigProperties systemConfigProperties;

	private static SystemConfigProperties systemConfig;

    @PostConstruct
    public void init() {
		environment=environmentProperties;
    	systemConfig=systemConfigProperties;
		CnaLogUtil.info(log,"CnaSysConfigUtil  initialized ！");
    }

    private static Map<String,LinkedHashMap<String, Object>> yamlCache=new ConcurrentHashMap<>();

	/**
	 * 获取环境上下文名称
	 * spring.profiles.active
	 * @author Lucifer
	 * @date 2023/3/9
	 * @since 1.0.0
	 * @return String
	 */
	public static <T> T getProfilesActive() {
		return getApplicationConfigByFullName("spring.profiles.active");
    }

	public static <T> T getApplicationName(){
		return getApplicationConfigByFullName("spring.application.name");
    }

	/**
	 * 获取上下文配置中的属性值
	 * @param configFullName 完整配置名称 例如：spring.application.name
	 * @return 属性值
	 */
	public static <T> T getApplicationConfigByFullName(String configFullName){
		T property = null;
		if (environment != null) {
			property = (T) environment.getProperty(configFullName);
		}
		if(ObjectUtils.isEmpty(property)){
			property = (T) getApplicationConfig(configFullName,false);
		}
		return property;
	}

	/**
	 * 获取上下文配置中的属性值 通过解析yml的方式，建议使用getEnvironmentValue
	 * @param environmentName 属性值名称
	 * @return 属性值
	 */
	private static <T> T getApplicationConfig(String environmentName , boolean recursion){
		LinkedHashMap<String, Object> sourceMap = null;
		sourceMap = getSourceMap("application.yml");
		if (ObjectUtils.isEmpty(sourceMap)) {
			sourceMap = getSourceMap("application.yaml");
		}
		T value = null;
		if(ObjectUtils.isNotEmpty(sourceMap)){
			value = (T) getString(sourceMap, environmentName);
			if (ObjectUtils.isEmpty(value) && !"spring.profiles.active".equals(environmentName)) {
				sourceMap=null;
				String environmentValue = getApplicationConfig("spring.profiles.active",true);
				if (StringUtils.isNotBlank(environmentValue)) {
					sourceMap = getSourceMap("application-"+environmentValue+".yml");
					if (ObjectUtils.isEmpty(sourceMap)) {
						sourceMap = getSourceMap("application-"+environmentValue+".yaml");
					}
				}
				if(ObjectUtils.isNotEmpty(sourceMap)){
					value = (T) getString(sourceMap, environmentName);
				}
			}
		}

		if (!recursion) {
			Assert.notNull(value, "配置文件中不存在此属性:"+environmentName);
		}
		return value;
	}

	/**
	 * 根据yaml文件名称获取配置map
	 * @author Lucifer
	 * @date 2023/3/9
	 * @since 1.0.0
	 * @param ymlName yml文件名称
	 * @return LinkedHashMap
	 */
	private static LinkedHashMap<String, Object> getSourceMap(String ymlName) {
		LinkedHashMap<String, Object> yamlMap = yamlCache.get(ymlName);
		if(ObjectUtils.isNotEmpty(yamlMap)) {
			return yamlMap;
		}

		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(ymlName);
		if (in != null) {
			Yaml yaml = new Yaml();
			yamlMap = yaml.load(in);
			yamlCache.put(ymlName,yamlMap);
		}
		return yamlMap;
	}

	/**
	 * 配置中遍历出需要的配置属性
	 * @author Lucifer
	 * @date 2023/3/9
	 * @since 1.0.0
	 * @param sourceMap 配置源
	 * @param key 目标属性
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	private static Object getString(LinkedHashMap<String, Object> sourceMap, String key) {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) sourceMap.clone();
		String[] keys = key.split("[.]");
		int length = keys.length;
		Object resultValue = null;
		for (int i = 0; i < length; i++) {
			Object value = map.get(keys[i]);
			if(value==null){
				return null;
			}
			if (i < length - 1) {
				map = ((LinkedHashMap<String, Object>) value);
			}else {
				resultValue = value;
			}
		}
		return resultValue;
	}

   /**
    * 获取Ip地址，则默认提供127.0.0.1
    * @author Lucifer
    * @date 2023/3/9
    * @since 1.0.0
    * @return String
    */
    public static String getHostIp() {
    	String ipString=null;
		try {
			ipString = InetAddress.getLocalHost().getHostAddress();
			if(StringUtils.isNotBlank(ipString)) {
				return ipString;
			}
		} catch (UnknownHostException e1) {
			log.error("获取IP地址1失败"+e1.getMessage(),e1);
		}
		ipString = extractedInetAddress();

		return ipString;
	}

	/**
	 * 获取Ip地址，容器内或者虚拟机无法提供IP地址，则默认提供127.0.0.1
	 * @author Lucifer
	 * @date 2023/3/9
	 * @since 1.0.0
	 * @return String
	 */
	private static String extractedInetAddress() {
		String ipString;
		InetAddress inetAddress = getIpAddress();
		if(inetAddress!=null && StringUtils.isNotBlank(inetAddress.getHostAddress())) {
			ipString=inetAddress.getHostAddress();
		}else {
			ipString="127.0.0.1";
		}
		return ipString;
	}

	/**
	 * 获取IP地址
	 * @author Lucifer
	 * @date 2023/3/9
	 * @since 1.0.0
	 * @return InetAddress
	 */
	private static InetAddress getIpAddress() {
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				if (!current.isUp() || current.isLoopback() || current.isVirtual()) {
					continue;
				}
				Enumeration<InetAddress> addresses = current.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					//去掉还回和虚拟地址
					if (addr.isLoopbackAddress()) {
						continue;
					}
					if (addr.isSiteLocalAddress()) {
						return addr;
					}
				}
			}
			throw new SocketException("Can't get our ip address, interfaces are: " + interfaces);
		} catch (SocketException e) {
			CnaLogUtil.error(log,"获取IP地址2失败"+e.getMessage(),e);
		}
		return null;
	}

	/**
	 * 获取 cnaworld.system-config 的配置项
	 * @author Lucifer
	 * @date 2023/3/9
	 * @since 1.0.0
	 * @param configName 配置项名称例如：configName
	 * @return 配置项值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getCnaConfigByName(String configName) {

		Object result;
		if (systemConfig != null){
			if(systemConfig.getConfigName() == null) {
				CnaLogUtil.error(log,"请检查 cnaworld.system-config 配置 : {}",configName);
				throw new RuntimeException("请检查 cnaworld.system-config 配置 : " + configName);
			}
			result = systemConfig.getConfigName().get(configName);
			if(ObjectUtils.isEmpty(result)) {
				CnaLogUtil.error(log,"cnaworld.system-config 没有此配置 : {}",configName);
				throw new RuntimeException("cnaworld.system-config 没有此配置 : "+configName);
			}
		}else {
			result = getApplicationConfigByFullName("cnaworld.system-config."+configName);
		}

		return (T) result;
	}

}

