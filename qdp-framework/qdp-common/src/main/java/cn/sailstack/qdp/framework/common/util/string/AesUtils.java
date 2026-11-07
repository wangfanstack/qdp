package cn.sailstack.qdp.framework.common.util.string;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 加解密工具：AES/ECB/PKCS5Padding，密文使用 Base64 编码
 *
 * 密钥长度支持 16 / 24 / 32 字节
 *
 * @author sailstack源码
 */
public class AesUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private AesUtils() {
    }

    /**
     * AES 加密，结果 Base64 编码
     *
     * @param data 明文
     * @param key  密钥（16/24/32 字节）
     * @return Base64 密文
     */
    public static String encrypt(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("AES 加密失败", e);
        }
    }

    /**
     * AES 解密
     *
     * @param base64Data Base64 密文
     * @param key        密钥（16/24/32 字节）
     * @return 明文
     */
    public static String decrypt(String base64Data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            return new String(cipher.doFinal(Base64.getDecoder().decode(base64Data)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("AES 解密失败", e);
        }
    }

}
