package cn.sailstack.qdp.framework.common.util.collection;

import cn.sailstack.qdp.framework.common.core.KeyValue;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Map 工具类
 *
 * @author 芋道源码
 */
public class MapUtils {

    public static <T, K> boolean isEmpty(Map<T, K> map) {
        return map == null || map.isEmpty();
    }

    public static <T, K> boolean isNotEmpty(Map<T, K> map) {
        return !isEmpty(map);
    }

    public static <K, V> List<V> getList(Multimap<K, V> multimap, Collection<K> keys) {
        List<V> result = new ArrayList<>();
        keys.forEach(k -> {
            Collection<V> values = multimap.get(k);
            if (values != null && !values.isEmpty()) {
                result.addAll(values);
            }
        });
        return result;
    }

    public static <K, V> void findAndThen(Map<K, V> map, K key, Consumer<V> consumer) {
        if (key == null || isEmpty(map)) {
            return;
        }
        V value = map.get(key);
        if (value == null) {
            return;
        }
        consumer.accept(value);
    }

    public static <K, V> Map<K, V> convertMap(List<KeyValue<K, V>> keyValues) {
        Map<K, V> map = Maps.newLinkedHashMapWithExpectedSize(keyValues.size());
        keyValues.forEach(keyValue -> map.put(keyValue.getKey(), keyValue.getValue()));
        return map;
    }

    public static <K> String getStr(Map<K, ?> map, K key) {
        return getStr(map, key, null);
    }

    public static <K> String getStr(Map<K, ?> map, K key, String defaultValue) {
        Object value = map == null ? null : map.get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    public static <K> Integer getInt(Map<K, ?> map, K key) {
        return getInt(map, key, null);
    }

    public static <K> Integer getInt(Map<K, ?> map, K key, Integer defaultValue) {
        Object value = map == null ? null : map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String str && !str.isBlank()) {
            try {
                return Integer.parseInt(str.trim());
            } catch (NumberFormatException ignored) {
                // 解析失败返回默认值
            }
        }
        return defaultValue;
    }

    public static <K> Long getLong(Map<K, ?> map, K key) {
        return getLong(map, key, null);
    }

    public static <K> Long getLong(Map<K, ?> map, K key, Long defaultValue) {
        Object value = map == null ? null : map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String str && !str.isBlank()) {
            try {
                return Long.parseLong(str.trim());
            } catch (NumberFormatException ignored) {
                // 解析失败返回默认值
            }
        }
        return defaultValue;
    }

    public static <K> Double getDouble(Map<K, ?> map, K key, Double defaultValue) {
        Object value = map == null ? null : map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String str && !str.isBlank()) {
            try {
                return Double.parseDouble(str.trim());
            } catch (NumberFormatException ignored) {
                // 解析失败返回默认值
            }
        }
        return defaultValue;
    }

    public static <K> Boolean getBool(Map<K, ?> map, K key, Boolean defaultValue) {
        Object value = map == null ? null : map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof String str && !str.isBlank()) {
            return Boolean.parseBoolean(str.trim());
        }
        return defaultValue;
    }

    public static <K> BigDecimal getBigDecimal(Map<K, ?> map, K key) {
        return getBigDecimal(map, key, null);
    }

    public static <K> BigDecimal getBigDecimal(Map<K, ?> map, K key, BigDecimal defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (value instanceof String str) {
            try {
                return new BigDecimal(str.trim());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }


    /**
     * 创建初始容量为 16 的 HashMap
     *
     * @param <K> 键类型
     * @param <V> 值类型
     * @return HashMap
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>(16);
    }

    /**
     * 创建指定初始容量的 HashMap；mapType 为 true 时使用 LinkedHashMap 保持插入顺序
     *
     * @param size            初始容量
     * @param orderLinked 是否保持插入顺序
     * @param <K>             键类型
     * @param <V>             值类型
     * @return Map
     */
    public static <K, V> Map<K, V> newHashMap(int size, boolean orderLinked) {
        return orderLinked ? new java.util.LinkedHashMap<>(size) : new HashMap<>(size);
    }

    /**
     * 移除 Map 中指定的多个 key
     *
     * @param map  Map
     * @param keys 待移除的 key
     * @param <K>  键类型
     * @param <V>  值类型
     * @return 被移除的键值对；无匹配时返回空 Map
     */
    public static <K, V> Map<K, V> removeAny(Map<K, V> map, K... keys) {
        Map<K, V> removed = new HashMap<>();
        if (isEmpty(map) || keys == null) {
            return removed;
        }
        for (K key : keys) {
            V value = map.remove(key);
            if (value != null) {
                removed.put(key, value);
            }
        }
        return removed;
    }

    /**
     * 创建链式 Map 构建器
     *
     * @param <K> 键类型
     * @param <V> 值类型
     * @return MapBuilder
     */
    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>(new LinkedHashMap<>());
    }

    /**
     * 链式 Map 构建器
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class MapBuilder<K, V> {

        private final Map<K, V> map;

        public MapBuilder(Map<K, V> map) {
            this.map = map;
        }

        public MapBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }
    }

    /**
     * 获取指定类型的值：String/Integer/Long/Double/Boolean/BigDecimal 自动转换，其余直接强转
     *
     * @param map  Map
     * @param key  键
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 转换后的值；不存在时返回 null
     */
    @SuppressWarnings("unchecked")
    public static <K, T> T get(Map<K, ?> map, K key, Class<T> type) {
        Object value = map == null ? null : map.get(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        if (type == String.class) {
            return (T) String.valueOf(value);
        }
        if (value instanceof Number number) {
            if (type == Integer.class) {
                return (T) Integer.valueOf(number.intValue());
            }
            if (type == Long.class) {
                return (T) Long.valueOf(number.longValue());
            }
            if (type == Double.class) {
                return (T) Double.valueOf(number.doubleValue());
            }
            if (type == java.math.BigDecimal.class) {
                return (T) java.math.BigDecimal.valueOf(number.doubleValue());
            }
        }
        if (value instanceof String str) {
            try {
                if (type == Integer.class) {
                    return (T) Integer.valueOf(str.trim());
                }
                if (type == Long.class) {
                    return (T) Long.valueOf(str.trim());
                }
                if (type == Double.class) {
                    return (T) Double.valueOf(str.trim());
                }
                if (type == Boolean.class) {
                    return (T) Boolean.valueOf(str.trim());
                }
                if (type == java.math.BigDecimal.class) {
                    return (T) new java.math.BigDecimal(str.trim());
                }
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (type == Boolean.class && value instanceof Boolean) {
            return (T) value;
        }
        return null;
    }

    /**
     * 拼接 Map 为字符串：key1=value1&key2=value2 形态
     *
     * @param map               Map
     * @param entrySeparator    条目分隔符
     * @param keyValueSeparator 键值分隔符
     * @param <K>               键类型
     * @param <V>               值类型
     * @return 拼接结果
     */
    public static <K, V> String join(Map<K, V> map, String entrySeparator, String keyValueSeparator) {
        if (isEmpty(map)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        map.forEach((k, v) -> {
            if (builder.length() > 0) {
                builder.append(entrySeparator);
            }
            builder.append(k).append(keyValueSeparator).append(v);
        });
        return builder.toString();
    }
}
