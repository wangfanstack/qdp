package cn.sailstack.qdp.framework.common.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * IO 工具类
 *
 * @author 芋道源码
 */
public class IoUtils {

    /**
     * 从流中读取 UTF-8 编码的内容
     *
     * @param in      输入流
     * @param isClose 是否关闭
     * @return 内容
     * @throws UncheckedIOException IO 异常
     */
    public static String readUtf8(InputStream in, boolean isClose) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            if (isClose) {
                try {
                    in.close();
                } catch (IOException ignored) {
                    // 关闭失败忽略
                }
            }
        }
    }

}
