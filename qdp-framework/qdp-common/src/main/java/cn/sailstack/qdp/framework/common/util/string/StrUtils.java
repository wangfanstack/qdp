package cn.sailstack.qdp.framework.common.util.string;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 *
 * @author 芋道源码
 */
public class StrUtils {

    public static String maxLength(CharSequence str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str.toString();
        }
        return str.subSequence(0, maxLength - 3) + "..."; // -3 的原因，是补充 ... 恰好
    }

    /**
     * 给定字符串是否以任何一个字符串开始
     * 给定字符串和数组为空都返回 false
     *
     * @param str      给定字符串
     * @param prefixes 需要检测的开始字符串
     */
    public static boolean startWithAny(String str, Collection<String> prefixes) {
        if (str == null || str.isEmpty() || prefixes == null || prefixes.isEmpty()) {
            return false;
        }

        for (CharSequence prefix : prefixes) {
            if (prefix != null && !prefix.isEmpty() && str.regionMatches(true, 0, prefix.toString(), 0, prefix.length())) {
                return true;
            }
        }
        return false;
    }

    public static List<Long> splitToLong(String value, CharSequence separator) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(value.split(String.valueOf(separator)))
                .filter(s -> !s.isBlank())
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toList());
    }

    public static Set<Long> splitToLongSet(String value) {
        return splitToLongSet(value, ",");
    }

    public static Set<Long> splitToLongSet(String value, CharSequence separator) {
        if (value == null || value.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(value.split(String.valueOf(separator)))
                .filter(s -> !s.isBlank())
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toSet());
    }

    public static List<Integer> splitToInteger(String value, CharSequence separator) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(value.split(String.valueOf(separator)))
                .filter(s -> !s.isBlank())
                .map(s -> Integer.parseInt(s.trim()))
                .collect(Collectors.toList());
    }

    /**
     * 移除字符串中，包含指定字符串的行
     *
     * @param content 字符串
     * @param sequence 包含的字符串
     * @return 移除后的字符串
     */
    public static String removeLineContains(String content, String sequence) {
        if (content == null || content.isEmpty() || sequence == null || sequence.isEmpty()) {
            return content;
        }
        return Arrays.stream(content.split("\n"))
                .filter(line -> !line.contains(sequence))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 拼接方法的参数
     *
     * 特殊：排除一些无法序列化的参数，如 ServletRequest、ServletResponse、MultipartFile
     *
     * @param joinPoint 连接点
     * @return 拼接后的参数
     */
    public static String joinMethodArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        // 排除无法序列化的参数类型，通过方法签名过滤，避免实际加载参数对象
        String[] paramTypeNames = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes() == null
                ? new String[0]
                : Arrays.stream(((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes())
                        .map(Class::getName).toArray(String[]::new);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            Object item = args[i];
            if (builder.length() > 0) {
                builder.append(',');
            }
            if (item == null || (i < paramTypeNames.length && isNotSerializableParamType(paramTypeNames[i]))) {
                continue;
            }
            String clazzName = item.getClass().getName();
            if (startWithAny(clazzName, List.of("javax.servlet", "jakarta.servlet", "org.springframework.web"))) {
                continue;
            }
            builder.append(item);
        }
        return builder.toString();
    }

    private static boolean isNotSerializableParamType(String typeName) {
        return typeName.startsWith("javax.servlet") || typeName.startsWith("jakarta.servlet")
                || typeName.startsWith("org.springframework.web");
    }

    /**
     * 格式化模板字符串，将 {} 占位符按参数顺序替换
     *
     * 例：format("用户 {} 的角色为 {}", "芋艿", 1) → "用户 芋艿 的角色为 1"
     *
     * @param template 模板字符串
     * @param params   参数
     * @return 格式化后的字符串
     */
    public static String format(String template, Object... params) {
        if (template == null || params == null || params.length == 0) {
            return template;
        }
        StringBuilder builder = new StringBuilder(template.length() + 32);
        int from = 0;
        int paramIndex = 0;
        int placeholder;
        while ((placeholder = template.indexOf("{}", from)) >= 0) {
            builder.append(template, from, placeholder);
            builder.append(paramIndex < params.length ? String.valueOf(params[paramIndex++]) : "{}");
            from = placeholder + 2;
        }
        builder.append(template.substring(from));
        return builder.toString();
    }

    /**
     * 以 UTF-8 编码获取字符串的字节
     *
     * @param str 字符串
     * @return 字节数组；null 传入返回 null
     */
    public static byte[] utf8Bytes(String str) {
        return str == null ? null : str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 以 UTF-8 编码构建字符串
     *
     * @param bytes 字节数组
     * @return 字符串；null 传入返回 null
     */
    public static String utf8Str(byte[] bytes) {
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }


    /**
     * null 返回默认值
     */
    public static String nullToDefault(String str, String defaultValue) {
        return str == null ? defaultValue : str;
    }

    /**
     * 空白（null/空串/空白串）返回默认值
     */
    public static String blankToDefault(String str, String defaultValue) {
        return (str == null || str.isBlank()) ? defaultValue : str;
    }

    /**
     * null 转空串
     */
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * 给定字符串是否以任何一个字符串开始
     *
     * @param str      给定字符串
     * @param prefixes 前缀
     * @return 是否以任一前缀开始
     */
    public static boolean startWithAny(String str, String... prefixes) {
        if (str == null || prefixes == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否以指定前缀开始（大小写不敏感）
     */
    public static boolean startWithIgnoreCase(String str, String prefix) {
        return str != null && prefix != null && str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * 是否以指定前缀开始
     */
    public static boolean startWith(String str, String prefix) {
        return str != null && str.startsWith(prefix);
    }

    /**
     * 截取分隔符首次出现后的内容；不含分隔符时返回 null
     */
    public static String subAfter(String str, String separator) {
        if (str == null) {
            return null;
        }
        int index = str.indexOf(separator);
        return index < 0 ? null : str.substring(index + separator.length());
    }

    /**
     * 截取分隔符首次出现前的内容；不含分隔符时返回 null
     */
    public static String subBefore(String str, String separator) {
        if (str == null) {
            return null;
        }
        int index = str.indexOf(separator);
        return index < 0 ? null : str.substring(0, index);
    }

    /**
     * 截取分隔符首次出现前的内容；不含分隔符时返回原串
     */
    public static String subBeforeOrAll(String str, String separator) {
        if (str == null) {
            return null;
        }
        int index = str.indexOf(separator);
        return index < 0 ? str : str.substring(0, index);
    }

    /**
     * 按分隔符切分字符串并去除每项首尾空白
     *
     * @param value     字符串
     * @param separator 分隔符
     * @return 切分结果；空值返回空列表
     */
    public static java.util.List<String> splitTrim(String value, String separator) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(value.split(java.util.regex.Pattern.quote(separator)))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 驼峰命名转下划线命名
     *
     * 例：browseCount → browse_count
     *
     * @param str 驼峰字符串
     * @return 下划线字符串
     */
    public static String toUnderlineCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 从尾部截取指定长度的字符串
     *
     * @param str    原串
     * @param length 长度
     * @return 尾部子串
     */
    public static String subSufByLength(String str, int length) {
        if (str == null || length <= 0 || str.length() < length) {
            return str;
        }
        return str.substring(str.length() - length);
    }
}
