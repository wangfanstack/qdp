package cn.sailstack.qdp.framework.common.util.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static cn.sailstack.qdp.framework.common.util.collection.CollectionUtils.convertList;

/**
 * Array 工具类
 *
 * @author 芋道源码
 */
public class ArrayUtils {

    /**
     * 将 object 和 newElements 合并成一个数组
     *
     * @param object 对象
     * @param newElements 数组
     * @param <T> 泛型
     * @return 结果数组
     */
    @SafeVarargs
    public static <T> Consumer<T>[] append(Consumer<T> object, Consumer<T>... newElements) {
        if (object == null) {
            return newElements;
        }
        @SuppressWarnings("unchecked")
        Consumer<T>[] result = (Consumer<T>[]) Array.newInstance(Consumer.class, 1 + newElements.length);
        result[0] = object;
        System.arraycopy(newElements, 0, result, 1, newElements.length);
        return result;
    }

    public static <T, V> V[] toArray(Collection<T> from, Function<T, V> mapper) {
        if (isEmpty(from)) {
            @SuppressWarnings("unchecked")
            V[] empty = (V[]) new Object[0];
            return empty;
        }
        java.util.List<V> list = convertList(from, mapper);
        @SuppressWarnings("unchecked")
        V[] array = (V[]) Array.newInstance(list.get(0).getClass(), list.size());
        return list.toArray(array);
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @param <T>   元素类型
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否非空
     *
     * @param array 数组
     * @param <T>   元素类型
     * @return 是否非空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static <T> T get(T[] array, int index) {
        if (index < 0 || array == null || index >= array.length) {
            return null;
        }
        return array[index];
    }

    /**
     * 数组是否包含指定元素
     *
     * @param array 数组
     * @param value 元素
     * @param <T>   元素类型
     * @return 是否包含
     */
    public static <T> boolean contains(T[] array, T value) {
        return array != null && Arrays.asList(array).contains(value);
    }

    /**
     * 返回数组中第一个符合条件的元素
     *
     * @param array     数组
     * @param predicate 条件
     * @param <T>       元素类型
     * @return 元素；不存在时返回 null
     */
    /**
     * 返回数组中第一个符合条件的元素（兼容三参签名）
     *
     * @param elementType 元素类型（保留兼容参数）
     * @param predicate   条件
     * @param array       数组
     * @param <T>         元素类型
     * @return 元素；不存在时返回 null
     */
    @SafeVarargs
    public static <T> T firstMatch(Class<T> elementType, Predicate<T> predicate, T... array) {
        return firstMatch(array, predicate);
    }

    /**
     * 返回数组中第一个符合条件的元素（lambda 在前、数组在后的调用形态）
     *
     * @param predicate 条件
     * @param array     数组
     * @param <T>       元素类型
     * @return 元素；不存在时返回 null
     */
    @SafeVarargs
    public static <T> T firstMatch(Predicate<T> predicate, T... array) {
        return firstMatch(array, predicate);
    }

    public static <T> T firstMatch(T[] array, Predicate<T> predicate) {
        if (isEmpty(array)) {
            return null;
        }
        for (T item : array) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }

    private static boolean isEmpty(Collection<?> from) {
        return from == null || from.isEmpty();
    }

}
