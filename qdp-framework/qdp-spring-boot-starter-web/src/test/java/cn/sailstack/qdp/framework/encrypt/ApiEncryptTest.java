package cn.sailstack.qdp.framework.encrypt;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 各种 API 加解密的测试类：不是单测，而是方便大家生成密钥、加密、解密等操作。
 * 加解密模式与 {@link ApiEncryptCipher} 保持一致：AES/ECB/PKCS5Padding、RSA/ECB/PKCS1Padding
 * （主代码 RSA 使用 BC 的 PKCS5Padding 别名，依赖运行时注册的 BouncyCastle Provider，测试采用 JDK 标准名）。
 * 原 Hutool 实现已迁移为 JDK Cipher 实现。
 *
 * @author 芋道源码
 */
@SuppressWarnings("ConstantValue")
public class ApiEncryptTest {

    @Test
    public void testGenerateAsymmetric() throws Exception {
        String requestClientKey;
        String requestServerKey;
        String responseClientKey;
        String responseServerKey;
        // 请求的密钥：客户端公钥 + 服务端私钥
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair requestKeyPair = generator.generateKeyPair();
        requestClientKey = Base64.getEncoder().encodeToString(requestKeyPair.getPublic().getEncoded());
        requestServerKey = Base64.getEncoder().encodeToString(requestKeyPair.getPrivate().getEncoded());
        // 响应的密钥：服务端公钥 + 客户端私钥
        KeyPair responseKeyPair = generator.generateKeyPair();
        responseClientKey = Base64.getEncoder().encodeToString(responseKeyPair.getPrivate().getEncoded());
        responseServerKey = Base64.getEncoder().encodeToString(responseKeyPair.getPublic().getEncoded());

        // 打印结果
        System.out.println("requestClientKey = " + requestClientKey);
        System.out.println("requestServerKey = " + requestServerKey);
        System.out.println("responseClientKey = " + responseClientKey);
        System.out.println("responseServerKey = " + responseServerKey);
    }

    @Test
    public void testEncrypt_aes() throws Exception {
        String key = "52549111389893486934626385991395";
        String body = "{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"password\": \"admin123\",\n" +
                "  \"uuid\": \"3acd87a09a4f48fb9118333780e94883\",\n" +
                "  \"code\": \"1024\"\n" +
                "}";
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        System.out.println("encrypt = " + encrypt);
    }

    @Test
    public void testEncrypt_rsa() throws Exception {
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCls2rIpnGdYnLFgz1XU13GbNQ5DloyPpvW00FPGjqn5Z6JpK+kDtVlnkhwR87iRrE5Vf2WNqRX6vzbLSgveIQY8e8oqGCb829myjf1MuI+ZzN4ghf/7tEYhZJGPI9AbfxFqBUzm+kR3/HByAI22GLT96WM26QiMK8n3tIP/yiLswIDAQAB";
        String body = "{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"password\": \"admin123\",\n" +
                "  \"uuid\": \"3acd87a09a4f48fb9118333780e94883\",\n" +
                "  \"code\": \"1024\"\n" +
                "}";
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        System.out.println("encrypt = " + encrypt);
    }

}
