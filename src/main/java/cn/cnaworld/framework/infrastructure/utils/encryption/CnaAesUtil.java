package cn.cnaworld.framework.infrastructure.utils.encryption;

import cn.cnaworld.framework.infrastructure.statics.enums.ParseCode;
import cn.cnaworld.framework.infrastructure.utils.code.CnaCodeParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES加密解密工具类
 * @author Lucifer
 * @date 2023/3/8
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("unchecked")
public class CnaAesUtil {
	
	private CnaAesUtil() {}
	
	private static final String KEY_AES = "AES";

	/**
	 * AES加密，默认采用16进制编码
	 * @author Lucifer
	 * @date 2023/3/8
	 * @since 1.0.0
	 * @param key 加密密码
	 * @param data 需要加密的内容
	 * @return String
	 */
	public static <T> T encrypt(String key,T data) throws Exception {
		return encrypt(key,data,ParseCode.Hex);
	}

	/**
	 * AES加密，默认采用16进制编码
	 * @author Lucifer
	 * @date 2023/3/8
	 * @since 1.0.0
	 * @param key 加密密码
	 * @param data 需要加密的内容
	 * @param parseCode 编码方式
	 * @return String
	 */
	public static <T> T encrypt(String key, T data,ParseCode parseCode) throws Exception {
		byte[] byteArray = CnaCodeParseUtil.toByteArray(data);
		byte[] bytes = doAes(key, byteArray, Cipher.ENCRYPT_MODE);
		if (bytes != null) {
			if(data instanceof String){
				switch (parseCode){
					case Base64:
						return (T) CnaCodeParseUtil.base64Encode(bytes);
					case Hex:
					default:
						return (T) CnaCodeParseUtil.parseByte2HexStr(bytes);
				}
			}else {
				return (T) CnaCodeParseUtil.toObject(bytes);
			}
		}
		return null;
	}

	/**
	 * AES解密，默认先采用16进制解码
	 * @author Lucifer
	 * @date 2023/3/8
	 * @since 1.0.0
	 * @param key 加密密码
	 * @param data 需要加密的内容
	 * @return String
	 */
	public static <T> T decrypt(String key, T data) throws Exception {
		return decrypt(key, data, ParseCode.Hex);
	}

	/**
	 * AES解密，默认先采用16进制解码
	 * @author Lucifer
	 * @date 2023/3/8
	 * @since 1.0.0
	 * @param data 需要加密的内容
	 * @param key 加密密码
	 * @param parseCode 编码类型
	 */
	public static <T> T decrypt(String key, T data, ParseCode parseCode) throws Exception {
		byte[] bytes;
		if (ObjectUtils.isNotEmpty(data)) {
			if (data instanceof String){
				switch (parseCode){
					case Base64:
						bytes = CnaCodeParseUtil.base64Decode((String) data);
						break;
					case Hex:
					default:
						bytes = CnaCodeParseUtil.parseHexStr2Byte((String) data);
				}
			}else {
				bytes = CnaCodeParseUtil.toByteArray(data);
			}
			byte[] result = doAes(key, bytes, Cipher.DECRYPT_MODE);
			return (T) CnaCodeParseUtil.toObject(result);
		}
		return null;
	}

	private static byte[] doAes(String key,byte[] content, int mode) throws Exception {
		if (ObjectUtils.isEmpty(content)){
			return null;
		}
		//1.构造密钥生成器，指定为AES算法,不区分大小写
		KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
		//2.根据ecnodeRules规则初始化密钥生成器
		//生成一个128位的随机源,根据传入的字节数组
		kgen.init(128, new SecureRandom(key.getBytes()));
		//3.产生原始对称密钥7
		SecretKey secretKey = kgen.generateKey();
		//4.获得原始对称密钥的字节数组
		byte[] enCodeFormat = secretKey.getEncoded();
		//5.根据字节数组生成AES密钥
		SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
		//6.根据指定算法AES自成密码器
		// 创建密码器
		Cipher cipher = Cipher.getInstance(KEY_AES);
		//7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
		// 初始化
		cipher.init(mode, keySpec);
		return cipher.doFinal(content);
	}

}
