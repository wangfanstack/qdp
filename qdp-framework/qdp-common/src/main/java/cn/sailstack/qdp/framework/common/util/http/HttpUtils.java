package cn.sailstack.qdp.framework.common.util.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * HTTP 工具类
 *
 * @author 芋道源码
 */
public class HttpUtils {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * 编码 URL 参数
     *
     * @param value 参数
     * @return 编码后的参数
     */
    public static String encodeUtf8(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * 解码 URL 参数（query parameter）
     * 注意：此方法会将 + 解码为空格，适用于 query parameter，不适用于 URL path
     *
     * @see #decodeUrlPath(String)
     * @param value 参数
     * @return 解码后的参数
     */
    public static String decodeUtf8(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    /**
     * 解码 URL 路径
     * 与 {@link #decodeUtf8(String)} 不同，此方法不会将 + 解码为空格，保持 + 为字面字符
     * 适用于 URL path 部分的解码
     *
     * @param path URL 路径
     * @return 解码后的路径
     */
    public static String decodeUrlPath(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        // 先将 + 替换为 %2B，避免被 URLDecoder 解码为空格
        String encoded = path.replace("+", "%2B");
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    /**
     * 编码 URL 路径，按路径段编码，保留 / 分隔符
     *
     * @param path URL 路径，例如 20250602/xxx.pdf
     * @return 编码后的路径
     */
    public static String encodeUrlPath(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        String[] segments = path.split("/", -1);
        StringBuilder result = new StringBuilder(path.length());
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                result.append('/');
            }
            result.append(encodeUrlPathSegment(segments[i]));
        }
        return result.toString();
    }

    /**
     * 编码 URL 路径段
     *
     * @param segment URL 路径段
     * @return 编码后的路径段
     */
    public static String encodeUrlPathSegment(String segment) {
        return UriUtils.encodePathSegment(segment, StandardCharsets.UTF_8);
    }

    public static String removeUrlPathQueryAndFragment(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        int endIndex = path.length();
        int queryIndex = path.indexOf('?');
        if (queryIndex >= 0) {
            endIndex = queryIndex;
        }
        int fragmentIndex = path.indexOf('#');
        if (fragmentIndex >= 0 && fragmentIndex < endIndex) {
            endIndex = fragmentIndex;
        }
        return path.substring(0, endIndex);
    }

    /**
     * 替换 URL 中同名 query 参数的值；不存在则追加
     *
     * @param url   URL
     * @param key   参数名
     * @param value 参数值
     * @return 替换后的 URL
     */
    public static String replaceUrlQuery(String url, String key, String value) {
        return UriComponentsBuilder.fromUriString(url).replaceQueryParam(key, value)
                .build().toUriString();
    }

    /**
     * 移除 URL 的 query 与 fragment 部分
     *
     * @param url URL
     * @return 移除后的 URL
     */
    public static String removeUrlQuery(String url) {
        if (url == null || !url.contains("?")) {
            return url;
        }
        return UriComponentsBuilder.fromUriString(url).replaceQuery(null)
                .fragment(null).build().toUriString();
    }

    /**
     * 拼接 URL
     *
     * copy from Spring Security OAuth2 的 AuthorizationEndpoint 类的 append 方法
     *
     * @param base 基础 URL
     * @param query 查询参数
     * @param keys query 的 key，对应的原本的 key 的映射。例如说 query 里有个 key 是 xx，实际它的 key 是 extra_xx，则通过 keys 里添加这个映射
     * @param fragment URL 的 fragment，即拼接到 # 中
     * @return 拼接后的 URL
     */
    public static String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {
        UriComponentsBuilder template = UriComponentsBuilder.newInstance();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
        URI redirectUri;
        try {
            // assume it's encoded to start with (if it came in over the wire)
            redirectUri = builder.build(true).toUri();
        } catch (Exception e) {
            // ... but allow client registrations to contain hard-coded non-encoded values
            redirectUri = builder.build().toUri();
            builder = UriComponentsBuilder.fromUri(redirectUri);
        }
        template.scheme(redirectUri.getScheme()).port(redirectUri.getPort()).host(redirectUri.getHost())
                .userInfo(redirectUri.getUserInfo()).path(redirectUri.getPath());

        if (fragment) {
            StringBuilder values = new StringBuilder();
            if (redirectUri.getFragment() != null) {
                String append = redirectUri.getFragment();
                values.append(append);
            }
            for (String key : query.keySet()) {
                if (values.length() > 0) {
                    values.append("&");
                }
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                values.append(name).append("={").append(key).append("}");
            }
            if (values.length() > 0) {
                template.fragment(values.toString());
            }
            UriComponents encoded = template.build().expand(query).encode();
            builder.fragment(encoded.getFragment());
        } else {
            for (String key : query.keySet()) {
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                template.queryParam(name, "{" + key + "}");
            }
            template.fragment(redirectUri.getFragment());
            UriComponents encoded = template.build().expand(query).encode();
            builder.query(encoded.getQuery());
        }
        return builder.build().toUriString();
    }

    public static String[] obtainBasicAuthorization(HttpServletRequest request) {
        String clientId;
        String clientSecret;
        // 先从 Header 中获取
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            authorization = subAfter(authorization, "Basic ");
        }
        if (StringUtils.hasText(authorization)) {
            authorization = new String(Base64.getDecoder().decode(authorization), StandardCharsets.UTF_8);
            clientId = subBefore(authorization, ":");
            clientSecret = subAfter(authorization, ":");
            // 再从 Param 中获取
        } else {
            clientId = request.getParameter("client_id");
            clientSecret = request.getParameter("client_secret");
        }

        // 如果两者非空，则返回
        if (isNotEmpty(clientId) && isNotEmpty(clientSecret)) {
            return new String[]{clientId, clientSecret};
        }
        return null;
    }

    /**
     * HTTP post 请求
     *
     * @param url URL
     * @param headers 请求头
     * @param requestBody 请求体
     * @return 请求结果
     */
    public static String post(String url, Map<String, String> headers, String requestBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8));
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return execute(builder);
    }

    /**
     * HTTP get 请求
     *
     * @param url URL
     * @param headers 请求头
     * @return 请求结果
     */
    public static String get(String url, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).GET();
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return execute(builder);
    }

    private static String execute(HttpRequest.Builder builder) {
        try {
            HttpResponse<String> response = CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * WebSocket URL 切换成 HTTP URL：ws:// → http://；wss:// → https://；其它格式原样保留
     *
     * @param url 原始 URL
     * @return 切换协议后的 URL
     */
    public static String wsUrlToHttp(String url) {
        return url != null && url.regionMatches(true, 0, "ws", 0, 2) ? "http" + url.substring(2) : url;
    }

    /**
     * 截取分隔符首次出现后的内容；不含分隔符时返回 null
     *
     * @param str       原字符串
     * @param separator 分隔符
     * @return 分隔符之后的内容
     */
    private static String subAfter(String str, String separator) {
        if (str == null) {
            return null;
        }
        int index = str.indexOf(separator);
        return index < 0 ? null : str.substring(index + separator.length());
    }

    /**
     * 截取分隔符首次出现前的内容；不含分隔符时返回 null
     *
     * @param str       原字符串
     * @param separator 分隔符
     * @return 分隔符之前的内容
     */
    private static String subBefore(String str, String separator) {
        if (str == null) {
            return null;
        }
        int index = str.indexOf(separator);
        return index < 0 ? null : str.substring(0, index);
    }

    private static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

}
