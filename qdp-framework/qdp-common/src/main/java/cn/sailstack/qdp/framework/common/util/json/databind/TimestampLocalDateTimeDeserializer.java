package cn.sailstack.qdp.framework.common.util.json.databind;

import cn.sailstack.qdp.framework.common.util.date.LocalDateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * LocalDateTime 反序列化器，支持毫秒时间戳、常见日期时间字符串和 ISO-8601 日期。
 *
 * @author 老五
 */
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("-?\\d+");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        text = text.trim();
        // 情况一：毫秒时间戳
        if (TIMESTAMP_PATTERN.matcher(text).matches()) {
            return LocalDateTimeUtils.of(Long.parseLong(text));
        }
        // 情况二：ISO-8601 日期；标准解析保留时区偏移与纳秒精度
        if (text.contains("T")) {
            return LocalDateTimeUtils.of(DateTimeFormatter.ISO_DATE_TIME.parseBest(
                    text, Instant::from, LocalDateTime::from));
        }
        // 情况三：常见日期时间字符串
        return LocalDateTimeUtils.parse(text);
    }

}
