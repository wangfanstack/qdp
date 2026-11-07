package cn.sailstack.qdp.framework.encrypt.core.crypto;

/**
 * 非对称加解密接口：加密使用公钥，解密使用私钥
 *
 * @author sailstack源码
 */
public interface AsymmetricCrypto {

    /**
     * 使用公钥加密，结果 Base64 编码
     *
     * @param data 明文
     * @return Base64 密文
     */
    String encrypt(String data);

    /**
     * 使用私钥解密 Base64 密文
     *
     * @param data Base64 密文
     * @return 明文
     */
    String decrypt(String data);

}
