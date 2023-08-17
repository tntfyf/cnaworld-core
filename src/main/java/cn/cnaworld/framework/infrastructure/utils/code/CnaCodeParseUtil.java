package cn.cnaworld.framework.infrastructure.utils.code;

import cn.cnaworld.framework.infrastructure.utils.log.CnaLogUtil;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * 编码格式转换
 * @author Lucifer
 * @date 2023/8/14
 * @since 1.1.3
 */
@Slf4j
public class CnaCodeParseUtil{

    /**
     * 将二进制转换成16进制
     * @author Lucifer
     * @date 2023/3/8
     * @since 1.0.0
     * @param buf 二进制数据
     * @return 16进制字符
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     * @author Lucifer
     * @date 2023/3/8
     * @since 1.0.0
     * @param hexStr 16进制内容
     * @return 二进制数组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * BASE64 编码
     * @author Lucifer
     * @date 2023/3/8
     * @since 1.0.0
     * @param buf 二进制数组
     * @return 编码后内容
     */
    public static String base64Encode(byte[] buf) {
        return new BASE64Encoder().encode(buf);
    }

    /**
     * BASE64 解码
     * @author Lucifer
     * @date 2023/3/8
     * @since 1.0.0
     * @param data 待解密内容
     * @return 二进制数组
     */
    public static byte[] base64Decode(String data) throws Exception{
        return new BASE64Decoder().decodeBuffer(data);
    }

    /**
     * 将Object对象转byte数组
     * @param obj byte数组的object对象
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException e) {
            CnaLogUtil.error(log,"toByteArray error " + e.getMessage(), e);
        }
        return bytes;
    }

    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            CnaLogUtil.error(log,"toObject error " + e.getMessage(), e);
        }
        return obj;
    }

    /**
     * 4位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

}
