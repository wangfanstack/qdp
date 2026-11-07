package cn.sailstack.qdp.framework.common.util.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Object 工具类
 *
 * @author 芋道源码
 */
public class ObjectUtils {

    /**
     * 复制对象，并忽略 Id 编号
     *
     * @param object 被复制对象
     * @param consumer 消费者，可以二次编辑被复制对象
     * @return 复制后的对象
     */
    public static <T> T cloneIgnoreId(T object, Consumer<T> consumer) {
        T result = clone(object);
        // 忽略 id 编号
        Field field = getField(object.getClass(), "id");
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(result, null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        // 二次编辑
        if (result != null) {
            consumer.accept(result);
        }
        return result;
    }

    /**
     * 克隆对象：优先走 JDK clone 机制，否则反射创建实例后浅拷贝同名字段
     *
     * @param object 被克隆对象
     * @param <T>    对象类型
     * @return 克隆后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(T object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Cloneable) {
            try {
                java.lang.reflect.Method cloneMethod = object.getClass().getMethod("clone");
                cloneMethod.setAccessible(true);
                return (T) cloneMethod.invoke(object);
            } catch (Exception ignored) {
                // 走反射浅拷贝兜底
            }
        }
        try {
            Constructor<T> constructor = (Constructor<T>) object.getClass().getDeclaredConstructor();
            constructor.setAccessible(true);
            T result = constructor.newInstance();
            Class<?> clazz = object.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(result, field.get(object));
                }
                clazz = clazz.getSuperclass();
            }
            return result;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("克隆对象失败：" + object.getClass().getName(), e);
        }
    }

    private static Field getField(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // 继续向父类查找
            }
        }
        return null;
    }

    public static <T extends Comparable<T>> T max(T obj1, T obj2) {
        if (obj1 == null) {
            return obj2;
        }
        if (obj2 == null) {
            return obj1;
        }
        return obj1.compareTo(obj2) > 0 ? obj1 : obj2;
    }

    @SafeVarargs
    public static <T> T defaultIfNull(T... array) {
        for (T item : array) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    /**
     * 字符串为 null 或空白时返回默认值
     *
     * @param str          字符串
     * @param defaultValue 默认值
     * @return 非空白字符串或默认值
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return (str == null || str.isBlank()) ? defaultValue : str;
    }

    @SafeVarargs
    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.asList(array).contains(obj);
    }

    @SafeVarargs
    public static <T> boolean notEqualsAny(T obj, T... array) {
        return !Arrays.asList(array).contains(obj);
    }

    public static boolean equals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    /**
     * {@link #equals(Object, Object)} 的别名，兼容既有调用习惯
     */
    public static boolean equal(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    public static boolean notEqual(Object obj1, Object obj2) {
        return !Objects.equals(obj1, obj2);
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 对象是否非空：兼容 String、Collection、Map、数组
     *
     * @param obj 对象
     * @return 是否非空
     */
    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String str) {
            return !str.isEmpty();
        }
        if (obj instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }
        if (obj instanceof Map<?, ?> map) {
            return !map.isEmpty();
        }
        if (obj.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(obj) > 0;
        }
        return true;
    }

    /**
     * 判断多个对象是否全部为空
     *
     * @param objs 对象数组
     * @return 是否全部为空
     */
    public static boolean isAllEmpty(Object... objs) {
        return Arrays.stream(objs).noneMatch(ObjectUtils::isNotEmpty);
    }

    /**
     * 判断多个对象是否全部非空
     *
     * @param objs 对象数组
     * @return 是否全部非空
     */
    public static boolean isAllNotEmpty(Object... objs) {
        return Arrays.stream(objs).allMatch(ObjectUtils::isNotEmpty);
    }

    public static boolean isNotAllEmpty(Object... objs) {
        return !isAllEmpty(objs);
    }

}
