package cn.sailstack.qdp.framework.common.util.servlet;

import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.common.util.http.HttpUtils;
import cn.sailstack.qdp.framework.common.util.json.JsonUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Map;

/**
 * 客户端工具类
 *
 * @author 芋道源码
 */
public class ServletUtils {

    /**
     * 返回 JSON 字符串
     *
     * @param response 响应
     * @param object   对象，会序列化成 JSON 字符串
     */
    @SuppressWarnings("deprecation") // 必须使用 APPLICATION_JSON_UTF8_VALUE，否则会乱码
    public static void writeJSON(HttpServletResponse response, Object object) {
        String content = JsonUtils.toJsonString(object);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            response.getWriter().write(content);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /**
     * @param request 请求
     * @return ua
     */
    public static String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "";
    }

    /**
     * 获得请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getUserAgent(request);
    }

    public static String getClientIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getClientIP(request);
    }

    public static boolean isJsonRequest(ServletRequest request) {
        return org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

    public static String getBody(HttpServletRequest request) {
        // 只有在 json 请求才读取，因为只有 CacheRequestBodyFilter 才会进行缓存，支持重复读取
        if (!isJsonRequest(request)) {
            return null;
        }
        try {
            return request.getReader().lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    public static byte[] getBodyBytes(HttpServletRequest request) {
        // 只有在 json 请求才读取，因为只有 CacheRequestBodyFilter 才会进行缓存，支持重复读取
        if (!isJsonRequest(request)) {
            return null;
        }
        try {
            return request.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    public static String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        // 优先代理透传头，最后回落到 remoteAddr
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : headers) {
            String value = request.getHeader(header);
            if (value != null && !value.isEmpty() && !"unknown".equalsIgnoreCase(value)) {
                // X-Forwarded-For 可能为多级代理逗号分隔，取第一个
                int index = value.indexOf(',');
                return index > 0 ? value.substring(0, index).trim() : value.trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 获得请求参数 Map
     *
     * @param request 请求
     * @return 参数 Map
     */
    public static Map<String, String> getParamMap(HttpServletRequest request) {
        Map<String, String> params = new java.util.LinkedHashMap<>();
        request.getParameterMap().forEach((key, values) -> params.put(key, values != null && values.length > 0 ? values[0] : null));
        return params;
    }

    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        Map<String, String> headers = new java.util.LinkedHashMap<>();
        java.util.Enumeration<String> names = request.getHeaderNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }

    public static void writeAttachment(HttpServletResponse response, String filename, byte[] content)
            throws IOException {
        response.getOutputStream().write(content);
        response.addHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(filename));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

}
