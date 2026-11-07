package cn.sailstack.qdp.framework.common.util.collection;

import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;

import java.util.Set;

/**
 * Set 工具类
 *
 * @author 芋道源码
 */
public class SetUtils {

    @SafeVarargs
    public static <T> Set<T> asSet(T... objs) {
        return CollectionUtils.newHashSet(objs);
    }

}
