package cn.sailstack.qdp.framework.encrypt.core.crypto;

import cn.sailstack.qdp.framework.common.util.string.AesUtils;

/**
 * AES 加解密实现：AES/ECB/PKCS5Padding，密文 Base64 编码
 *
 * @author sailstack源码
 */
public class AesCrypto implements SymmetricCrypto {

    private final String key;

    public AesCrypto(byte[] keyBytes) {
        this.key = new String(keyBytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public String encrypt(String data) {
        return AesUtils.encrypt(data, key);
    }

    @Override
    public String decrypt(String data) {
        return AesUtils.decrypt(data, key);
    }

}
