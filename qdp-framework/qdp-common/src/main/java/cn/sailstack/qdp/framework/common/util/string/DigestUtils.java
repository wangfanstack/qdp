package cn.sailstack.qdp.framework.common.util.string;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * 摘要算法工具类（替代 hutool-crypto 的 SecureUtil / DigestUtil）
 *
 * @author sailstack源码
 */
public class DigestUtils {

    private DigestUtils() {
    }

    /**
     * 计算 MD5 摘要，返回 32 位小写 Hex 字符串
     *
     * @param text 原文
     * @return 摘要
     */
    public static String md5Hex(String text) {
        return digestHex("MD5", text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算 SHA-256 摘要，返回小写 Hex 字符串
     *
     * @param text 原文
     * @return 摘要
     */
    public static String sha256Hex(String text) {
        return digestHex("SHA-256", text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算 SHA-256 摘要，返回小写 Hex 字符串
     *
     * @param bytes 原始字节
     * @return 摘要
     */
    public static String sha256Hex(byte[] bytes) {
        return digestHex("SHA-256", bytes);
    }

    private static String digestHex(String algorithm, byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("摘要算法不可用：" + algorithm, e);
        }
    }


    /**
     * 计算字节数组的 MD5 摘要并转为小写十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String md5Hex(byte[] bytes) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(bytes);
            StringBuilder builder = new StringBuilder(result.length * 2);
            for (byte b : result) {
                builder.append(Character.forDigit((b >> 4) & 0xF, 16));
                builder.append(Character.forDigit(b & 0xF, 16));
            }
            return builder.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
