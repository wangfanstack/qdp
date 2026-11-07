package cn.sailstack.qdp.framework.common.util.collection;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.common.util.number.NumberUtils;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

/**
 * Collection 工具类
 *
 * @author 芋道源码
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean containsAny(Object source, Object... targets) {
        return asList(targets).contains(source);
    }

    public static boolean isAnyEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollectionUtils::isEmpty);
    }

    public static <T> boolean anyMatch(Collection<T> from, Predicate<T> predicate) {
        return from.stream().anyMatch(predicate);
    }

    public static <T> long sum(Collection<T> from, ToLongFunction<T> valueFunc) {
        if (isEmpty(from)) {
            return 0L;
        }
        return from.stream().mapToLong(valueFunc).sum();
    }

    public static <T> BigDecimal sumBigDecimal(Collection<T> from, Function<T, BigDecimal> valueFunc) {
        if (isEmpty(from)) {
            return BigDecimal.ZERO;
        }
        return from.stream().map(valueFunc).map(NumberUtils::zeroIfNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static <T> long count(Collection<T> from, Predicate<T> predicate) {
        if (isEmpty(from)) {
            return 0L;
        }
        return from.stream().filter(predicate).count();
    }

    public static <T, R> long distinctCount(Collection<T> from, Function<T, R> keyMapper) {
        if (isEmpty(from)) {
            return 0L;
        }
        return from.stream().map(keyMapper).filter(Objects::nonNull).distinct().count();
    }

    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return distinct(from, keyMapper, (t1, t2) -> t1);
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        if (from == null || from.length == 0) {
            return new ArrayList<>();
        }
        return convertList(Arrays.asList(from), func);
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> PageResult<U> convertPage(PageResult<T> from, Function<T, U> func) {
        if (from == null || isEmpty(from.getList())) {
            return new PageResult<>(from != null ? from.getTotal() : 0L);
        }
        return new PageResult<>(convertList(from.getList(), func), from.getTotal());
    }

    public static <T, U> List<U> convertListByFlatMap(Collection<T> from,
                                                      Function<T, ? extends Stream<? extends U>> func) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from,
                                                         Function<? super T, ? extends U> mapper,
                                                         Function<U, ? extends Stream<? extends R>> func) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().map(mapper).filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
        return map.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static <T> Set<T> convertSet(Collection<T> from) {
        return convertSet(from, v -> v);
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        if (isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U, S extends Set<U>> S convertSetBySupplier(
            Collection<T> from, Function<T, U> func, Supplier<S> supplier) {
        if (isEmpty(from)) {
            return supplier.get();
        }
        return from.stream().map(func).filter(Objects::nonNull)
                .collect(Collectors.toCollection(supplier));
    }

    public static <T, U> Set<U> convertLinkedSet(Collection<T> from, Function<T, U> func) {
        if (isEmpty(from)) {
            return new LinkedHashSet<>();
        }
        return from.stream().map(func).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static <T, U> Set<U> convertLinkedSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (isEmpty(from)) {
            return new LinkedHashSet<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static <T, K> Map<K, T> convertMapByFilter(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().filter(filter).collect(Collectors.toMap(keyFunc, v -> v));
    }

    public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from,
                                                    Function<T, ? extends Stream<? extends U>> func) {
        if (isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from,
                                                       Function<? super T, ? extends U> mapper,
                                                       Function<U, ? extends Stream<? extends R>> func) {
        if (isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(mapper).filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, Function.identity());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        if (isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, Function.identity(), supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        if (isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }

    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList())));
    }

    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream()
                .collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }

    // 暂时没想好名字，先以 2 结尾噶
    public static <T, K, V> Map<K, Set<V>> convertMultiMap2(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet())));
    }

    public static <T, K> Map<K, T> convertImmutableMap(Collection<T> from, Function<T, K> keyFunc) {
        if (isEmpty(from)) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<K, T> builder = ImmutableMap.builder();
        from.forEach(item -> builder.put(keyFunc.apply(item), item));
        return builder.build();
    }

    /**
     * 对比老、新两个列表，找出新增、修改、删除的数据
     *
     * @param oldList  老列表
     * @param newList  新列表
     * @param sameFunc 对比函数，返回 true 表示相同，返回 false 表示不同
     *                 注意，same 是通过每个元素的“标识”，判断它们是不是同一个数据
     * @return [新增列表、修改列表、删除列表]
     */
    public static <T> List<List<T>> diffList(Collection<T> oldList, Collection<T> newList,
                                             BiFunction<T, T, Boolean> sameFunc) {
        List<T> createList = new LinkedList<>(newList); // 默认都认为是新增的，后续会进行移除
        List<T> updateList = new ArrayList<>();
        List<T> deleteList = new ArrayList<>();

        // 通过以 oldList 为主遍历，找出 updateList 和 deleteList
        for (T oldObj : oldList) {
            // 1. 寻找是否有匹配的
            T foundObj = null;
            for (Iterator<T> iterator = createList.iterator(); iterator.hasNext(); ) {
                T newObj = iterator.next();
                // 1.1 不匹配，则直接跳过
                if (!sameFunc.apply(oldObj, newObj)) {
                    continue;
                }
                // 1.2 匹配，则移除，并结束寻找
                iterator.remove();
                foundObj = newObj;
                break;
            }
            // 2. 匹配添加到 updateList；不匹配则添加到 deleteList 中
            if (foundObj != null) {
                updateList.add(foundObj);
            } else {
                deleteList.add(oldObj);
            }
        }
        return asList(createList, updateList, deleteList);
    }

    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        return org.springframework.util.CollectionUtils.containsAny(source, candidates);
    }

    /**
     * 获取集合中指定下标的元素，越界或集合为空时返回 null
     *
     * @param from  集合
     * @param index 下标
     * @param <T>   元素类型
     * @return 元素
     */
    public static <T> T get(Collection<T> from, int index) {
        if (isEmpty(from) || index < 0 || index >= from.size()) {
            return null;
        }
        return from.stream().skip(index).findFirst().orElse(null);
    }

    public static <T> T getFirst(List<T> from) {
        return !isEmpty(from) ? from.get(0) : null;
    }

    public static <T> T getLast(List<T> from) {
        return !isEmpty(from) ? from.get(from.size() - 1) : null;
    }

    public static <T> T findFirst(Collection<T> from, Predicate<T> predicate) {
        return findFirst(from, predicate, Function.identity());
    }

    public static <T, U> U findFirst(Collection<T> from, Predicate<T> predicate, Function<T, U> func) {
        if (isEmpty(from)) {
            return null;
        }
        return from.stream().filter(predicate).findFirst().map(func).orElse(null);
    }

    /**
     * 查找第一个符合条件的元素
     *
     * @param from      集合
     * @param predicate 条件
     * @param <T>       元素类型
     * @return 元素；不存在时返回 null
     */
    public static <T> T findOne(Collection<T> from, Predicate<T> predicate) {
        if (isEmpty(from)) {
            return null;
        }
        return from.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * 集合是否包含指定元素（null 容错）
     *
     * @param collection 集合
     * @param value      元素
     * @param <T>        元素类型
     * @return 是否包含
     */
    public static <T> boolean contains(Collection<T> collection, T value) {
        return !isEmpty(collection) && collection.contains(value);
    }

    /**
     * 拼接集合元素为字符串
     *
     * @param from      集合
     * @param delimiter 分隔符
     * @param <T>       元素类型
     * @return 拼接结果
     */
    public static <T> String join(Iterable<T> from, String delimiter) {
        if (from == null) {
            return null;
        }
        return StreamSupport.stream(from.spliterator(), false)
                .map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(T... values) {
        List<T> list = new ArrayList<>(values != null ? values.length : 0);
        if (values != null) {
            list.addAll(Arrays.asList(values));
        }
        return list;
    }

    @SafeVarargs
    public static <T> Set<T> newHashSet(T... values) {
        Set<T> set = new HashSet<>(values != null ? values.length : 0);
        if (values != null) {
            set.addAll(Arrays.asList(values));
        }
        return set;
    }

    @SafeVarargs
    public static <T> Set<T> newLinkedHashSet(T... values) {
        Set<T> set = new LinkedHashSet<>(values != null ? values.length : 0);
        if (values != null) {
            set.addAll(Arrays.asList(values));
        }
        return set;
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item == null) {
            return;
        }
        coll.add(item);
    }

    public static <T> Collection<T> singleton(T obj) {
        return obj == null ? Collections.emptyList() : Collections.singleton(obj);
    }

    public static <T> List<T> newArrayList(List<List<T>> list) {
        return list.stream().filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static boolean dfs(Long node, Map<Long, Set<Long>> graph) {
        return dfs(node, graph, new HashSet<>(), new HashSet<>());
    }

    private static boolean dfs(Long node, Map<Long, Set<Long>> graph, Set<Long> visited, Set<Long> inStack) {
        if (inStack.contains(node)) {
            return true;
        }
        if (visited.contains(node)) {
            return false;
        }
        visited.add(node);
        inStack.add(node);
        Set<Long> neighbors = graph.getOrDefault(node, Collections.emptySet());
        for (Long neighbor : neighbors) {
            if (dfs(neighbor, graph, visited, inStack)) {
                return true;
            }
        }
        inStack.remove(node);
        return false;
    }

    /**
     * 把单元素 head 与集合 tail 合并成新 List（head 在前，tail 顺序保留）
     */
    public static <T> List<T> of(T head, Collection<T> tail) {
        List<T> list = new ArrayList<>();
        list.add(head);
        if (isNotEmpty(tail)) {
            list.addAll(tail);
        }
        return list;
    }


    /**
     * 拼接集合元素为字符串，元素经映射函数转换
     *
     * @param from      集合
     * @param delimiter 分隔符
     * @param func      元素映射
     * @param <T>       元素类型
     * @param <U>       映射结果类型
     * @return 拼接结果
     */
    public static <T, U> String join(Iterable<T> from, String delimiter, java.util.function.Function<T, U> func) {
        if (from == null) {
            return null;
        }
        return StreamSupport.stream(from.spliterator(), false)
                .map(func).filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    /**
     * 返回两个集合的交集（去重）
     *
     * @param collection1 集合 1
     * @param collection2 集合 2
     * @param <T>         元素类型
     * @return 交集列表
     */
    public static <T> List<T> intersectionDistinct(Collection<T> collection1, Collection<T> collection2) {
        if (isEmpty(collection1) || isEmpty(collection2)) {
            return new ArrayList<>();
        }
        Set<T> set2 = new HashSet<>(collection2);
        return collection1.stream().filter(set2::contains).distinct().collect(Collectors.toList());
    }

    /**
     * 获得集合中指定数值字段的最小值
     *
     * @param from      集合
     * @param valueFunc 数值映射
     * @param <T>       元素类型
     * @return 最小值；集合为空时返回 null
     */

    /**
     * 获得集合中指定数值字段的总和
     *
     * @param from      集合
     * @param valueFunc 数值映射
     * @param <T>       元素类型
     * @return 总和；集合为空时返回 null
     */

    public static <T, V extends Comparable<? super V>> V getMaxValue(Collection<T> from, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return null;
        }
        T t = from.stream().max(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> V getMinValue(Collection<T> from, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return null;
        }
        T t = from.stream().min(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc,
                                                                     BinaryOperator<V> accumulator) {
        return getSumValue(from, valueFunc, accumulator, null);
    }

    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc,
                                                                     BinaryOperator<V> accumulator, V defaultValue) {
        if (isEmpty(from)) {
            return defaultValue;
        }
        return from.stream().map(valueFunc).filter(Objects::nonNull).reduce(accumulator).orElse(defaultValue);
    }

    public static <T, V extends Comparable<? super V>> T getMinObject(List<T> from, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return null;
        }
        return from.stream().min(Comparator.comparing(valueFunc)).orElse(null);
    }

    /**
     * 合并两个集合并去重，保持顺序
     *
     * @param collection1 集合 1
     * @param collection2 集合 2
     * @param <T>         元素类型
     * @return 去重后的合并列表
     */
    public static <T> List<T> unionDistinct(Collection<T> collection1, Collection<T> collection2) {
        List<T> result = new ArrayList<>();
        if (isNotEmpty(collection1)) {
            result.addAll(collection1);
        }
        if (isNotEmpty(collection2)) {
            for (T item : collection2) {
                if (!result.contains(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * 获得集合大小；null 返回 0
     *
     * @param collection 集合
     * @param <T>        元素类型
     * @return 大小
     */
    public static <T> int size(Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }
}
