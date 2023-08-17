package cn.cnaworld.framework.infrastructure.utils.encryption;

import cn.cnaworld.framework.infrastructure.utils.code.CnaCodeParseUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Rsa加密解密工具类
 * @author Lucifer
 * @date 2023/8/13
 * @since 1.1.3
 */
public class CnaRsaUtil {

    public static String RSA_ALGORITHM = "RSA";

    public static String CHAR_SET_NAME = "UTF-8";

    /**
     * 密钥长度，DSA算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     * */
    private static final int KEY_SIZE=1024;

    /**
     * 生成密钥对
     * @return 密钥对对象
     */
    public static KeyStore createKeys() throws NoSuchAlgorithmException {
        //KeyPairGenerator用于生成公钥和私钥对。密钥对生成器是使用 getInstance 工厂方法
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        byte[] publicKeyByte = publicKey.getEncoded();
        byte[] privateKeyByte = privateKey.getEncoded();
        return new KeyStore(CnaCodeParseUtil.parseByte2HexStr(publicKeyByte), CnaCodeParseUtil.parseByte2HexStr(privateKeyByte));
    }

    /**
     * 私钥加密
     * @param key 密钥
     * @param data 数据
     * */
    public static byte[] encryptByPrivateKey(String key,byte[] data) throws Exception {
        handlePrivateKey result = getHandlePrivateKey(key);
        result.cipher.init(Cipher.ENCRYPT_MODE, result.privateKey);
        return result.cipher.doFinal(data);
    }

    /**
     * 公钥加密
     * @param key 密钥
     * @param data 数据
     */
    private static byte[] encryptByPublicKey(String key,byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        handlePublicKey result = getHandlePublicKey(key);
        result.cipher.init(Cipher.ENCRYPT_MODE, result.publicKey);
        return result.cipher.doFinal(data);
    }

    /**
     * 私钥解密
     * @param key 密钥
     * @param data 数据
     * */
    public static String decryptByPrivateKey(String key,byte[] data) throws Exception {
        handlePrivateKey result = getHandlePrivateKey(key);
        result.cipher.init(Cipher.DECRYPT_MODE, (Key) result);
        return new String(result.cipher.doFinal(data),CHAR_SET_NAME);
    }

    /**
     * 公钥解密
     * @param key 密钥
     * @param data 数据
     * */
    public static String decryptByPublicKey(String key,byte[] data) throws Exception {
        handlePublicKey result = getHandlePublicKey(key);
        result.cipher.init(Cipher.DECRYPT_MODE, result.publicKey);
        return new String(result.cipher.doFinal(data),CHAR_SET_NAME);
    }
    @Getter
    //定义密钥类
    @AllArgsConstructor
    public static class KeyStore {
        private String publicKey;
        private String privateKey;
    }

    private static handlePublicKey getHandlePublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        byte[] bytes = CnaCodeParseUtil.parseHexStr2Byte(key);
        X509EncodedKeySpec x509EncodedKeySpec = null;
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        //初始化公钥,根据给定的编码密钥创建一个新的 X509EncodedKeySpec。
        if (bytes != null) {
            x509EncodedKeySpec = new X509EncodedKeySpec(bytes);
        }
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        return new handlePublicKey(publicKey, cipher);
    }

    private static class handlePublicKey {
        public final PublicKey publicKey;
        public final Cipher cipher;

        public handlePublicKey(PublicKey publicKey, Cipher cipher) {
            this.publicKey = publicKey;
            this.cipher = cipher;
        }
    }

    private static handlePrivateKey getHandlePrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        byte[] bytes = CnaCodeParseUtil.parseHexStr2Byte(key);
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec= null;
        if (bytes != null) {
            pkcs8KeySpec = new PKCS8EncodedKeySpec(bytes);
        }
        KeyFactory keyFactory=KeyFactory.getInstance(RSA_ALGORITHM);
        //生成私钥
        PrivateKey privateKey=keyFactory.generatePrivate(pkcs8KeySpec);
        //数据加密
        Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
        return new handlePrivateKey(privateKey, cipher);
    }

    private static class handlePrivateKey {
        public final PrivateKey privateKey;
        public final Cipher cipher;

        public handlePrivateKey(PrivateKey privateKey, Cipher cipher) {
            this.privateKey = privateKey;
            this.cipher = cipher;
        }
    }

}
