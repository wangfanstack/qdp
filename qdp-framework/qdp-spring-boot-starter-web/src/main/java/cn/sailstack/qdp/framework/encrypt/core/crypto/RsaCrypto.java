package cn.sailstack.qdp.framework.encrypt.core.crypto;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 加解密实现：RSA/ECB/PKCS1Padding，密文 Base64 编码
 *
 * @author sailstack源码
 */
public class RsaCrypto implements AsymmetricCrypto {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RsaCrypto(String privateKeyBase64, String publicKeyBase64) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            this.privateKey = privateKeyBase64 != null
                    ? factory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64)))
                    : null;
            this.publicKey = publicKeyBase64 != null
                    ? factory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64)))
                    : null;
        } catch (Exception e) {
            throw new IllegalStateException("RSA 密钥解析失败", e);
        }
    }

    @Override
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("RSA 加密失败", e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("RSA 解密失败", e);
        }
    }

}
