package cn.sailstack.qdp.framework.encrypt.core.filter;

import cn.sailstack.qdp.framework.common.util.string.StrUtils;
import cn.sailstack.qdp.framework.encrypt.core.crypto.AsymmetricCrypto;
import cn.sailstack.qdp.framework.encrypt.core.crypto.SymmetricCrypto;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 解密请求 {@link HttpServletRequestWrapper} 实现类
 *
 * @author 芋道源码
 */
public class ApiDecryptRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public ApiDecryptRequestWrapper(HttpServletRequest request, SymmetricCrypto symmetricDecryptor, AsymmetricCrypto asymmetricDecryptor) throws IOException {
        super(request);
        // 读取 body，允许 HEX、BASE64 传输
        String requestBody = StrUtils.utf8Str(request.getInputStream().readAllBytes());

        // 解密 body
        String decryptedBody = symmetricDecryptor != null ? symmetricDecryptor.decrypt(requestBody)
                : asymmetricDecryptor.decrypt(requestBody);
        body = decryptedBody.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream stream = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public int read() {
                return stream.read();
            }

            @Override
            public int available() {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

        };
    }
}
