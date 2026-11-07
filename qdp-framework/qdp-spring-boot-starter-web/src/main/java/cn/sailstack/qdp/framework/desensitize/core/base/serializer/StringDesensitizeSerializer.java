package cn.sailstack.qdp.framework.desensitize.core.base.serializer;

import cn.sailstack.qdp.framework.common.util.collection.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.desensitize.core.base.annotation.DesensitizeBy;
import cn.sailstack.qdp.framework.desensitize.core.base.handler.DesensitizationHandler;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 脱敏序列化器
 *
 * 实现 JSON 返回数据时，使用 {@link DesensitizationHandler} 对声明脱敏注解的字段，进行脱敏处理。
 *
 * @author gaibu
 */
@SuppressWarnings("rawtypes")
public class StringDesensitizeSerializer extends StdSerializer<String> implements ContextualSerializer {

    @Getter
    @Setter
    private DesensitizationHandler desensitizationHandler;

    protected StringDesensitizeSerializer() {
        super(String.class);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) {
        DesensitizeBy annotation = beanProperty.getAnnotation(DesensitizeBy.class);
        if (annotation == null) {
            return this;
        }
        // 创建一个 StringDesensitizeSerializer 对象，使用 DesensitizeBy 对应的处理器
        StringDesensitizeSerializer serializer = new StringDesensitizeSerializer();
        try {
            serializer.setDesensitizationHandler(annotation.handler().getDeclaredConstructor().newInstance());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return serializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.isBlank(value)) {
            gen.writeNull();
            return;
        }
        // 获取序列化字段
        Field field = getField(gen);

        // 自定义处理器
        java.util.List<DesensitizeBy> annotationList = new java.util.ArrayList<>();
        for (java.lang.annotation.Annotation fieldAnnotation : field.getAnnotations()) {
            if (fieldAnnotation instanceof DesensitizeBy) {
                annotationList.add((DesensitizeBy) fieldAnnotation);
            } else {
                // 元注解上携带 DesensitizeBy 的组合注解
                for (java.lang.annotation.Annotation meta : fieldAnnotation.annotationType().getAnnotations()) {
                    if (meta instanceof DesensitizeBy) {
                        annotationList.add((DesensitizeBy) meta);
                    }
                }
            }
        }
        DesensitizeBy[] annotations = annotationList.toArray(new DesensitizeBy[0]);
        if (ArrayUtils.isEmpty(annotations)) {
            gen.writeString(value);
            return;
        }
        for (Annotation annotation : field.getAnnotations()) {
            boolean hasDesensitizeBy = false;
            if (annotation instanceof DesensitizeBy) {
                hasDesensitizeBy = true;
            } else {
                for (java.lang.annotation.Annotation meta : annotation.annotationType().getAnnotations()) {
                    if (meta instanceof DesensitizeBy) {
                        hasDesensitizeBy = true;
                        break;
                    }
                }
            }
            if (hasDesensitizeBy) {
                value = this.desensitizationHandler.desensitize(value, annotation);
                gen.writeString(value);
                return;
            }
        }
        gen.writeString(value);
    }

    /**
     * 获取字段
     *
     * @param generator JsonGenerator
     * @return 字段
     */
    private Field getField(JsonGenerator generator) {
        String currentName = generator.getOutputContext().getCurrentName();
        Object currentValue = generator.currentValue();
        Class<?> currentValueClass = currentValue.getClass();
        try {
            Class<?> c = currentValueClass;
            while (c != null && c != Object.class) {
                try {
                    return c.getDeclaredField(currentName);
                } catch (NoSuchFieldException ignored) {
                    c = c.getSuperclass();
                }
            }
            return null;
        } catch (SecurityException e) {
            return null;
        }
    }

}
