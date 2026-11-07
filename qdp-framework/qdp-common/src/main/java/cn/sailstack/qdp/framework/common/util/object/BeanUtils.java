package cn.sailstack.qdp.framework.common.util.object;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * Bean 工具类（过渡）
 *
 * 基于 Spring {@link org.springframework.beans.BeanUtils} 实现同名属性浅拷贝。
 * 新增转换请优先使用各模块的 XxxConvert（MapStruct）。
 *
 * @author sailstack源码
 */
public class BeanUtils {

    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target;
        try {
            target = targetClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("实例化对象失败：" + targetClass.getName(), e);
        }
        copyProperties(source, target);
        return target;
    }

    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return CollectionUtils.convertList(source, s -> toBean(s, targetType));
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);
        if (list != null) {
            list.forEach(peek);
        }
        return list;
    }

    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType) {
        return toBean(source, targetType, null);
    }

    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType, Consumer<T> peek) {
        if (source == null) {
            return null;
        }
        List<T> list = toBean(source.getList(), targetType);
        if (peek != null) {
            list.forEach(peek);
        }
        return new PageResult<>(list, source.getTotal());
    }

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }



    /**
     * List 浅拷贝转换（每个元素独立拷贝）
     */
    public static <S, T> java.util.List<T> toList(java.util.List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return cn.sailstack.qdp.framework.common.util.collection.CollectionUtils.convertList(source, s -> toBean(s, targetType));
    }

    /**
     * PageResult 转换
     */
    public static <S, T> cn.sailstack.qdp.framework.common.pojo.PageResult<T> toPage(cn.sailstack.qdp.framework.common.pojo.PageResult<S> source, Class<T> targetType) {
        return toBean(source, targetType);
    }
}
