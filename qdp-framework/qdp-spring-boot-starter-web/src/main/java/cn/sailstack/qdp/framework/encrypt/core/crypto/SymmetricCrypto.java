package cn.sailstack.qdp.framework.encrypt.core.crypto;

/**
 * 对称加解密接口
 *
 * @author sailstack源码
 */
public interface SymmetricCrypto {

    /**
     * 加密字符串，结果 Base64 编码
     *
     * @param data 明文
     * @return Base64 密文
     */
    String encrypt(String data);

    /**
     * 解密 Base64 密文
     *
     * @param data Base64 密文
     * @return 明文
     */
    String decrypt(String data);

}
