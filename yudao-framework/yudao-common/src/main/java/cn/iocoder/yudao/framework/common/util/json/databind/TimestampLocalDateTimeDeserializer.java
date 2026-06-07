package cn.iocoder.yudao.framework.common.util.json.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 基于时间戳的 LocalDateTime 反序列化器
 *
 * 支持两种格式：
 * 1. Long 时间戳（毫秒），例如 1293724800000
 * 2. ISO 标准字符串，例如 "2010-12-31T00:00:00"
 *
 * @author 老五
 */
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 情况一：Long 时间戳
        if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getLongValue()), ZoneId.systemDefault());
        }
        // 情况二：ISO 标准字符串 (如 "2010-12-31T00:00:00")
        String text = p.getValueAsString();
        if (text != null) {
            return LocalDateTime.parse(text, ISO_FORMATTER);
        }
        // 兜底：返回 null 而非 epoch 0，避免悄无声息的数据错误
        return null;
    }

}
