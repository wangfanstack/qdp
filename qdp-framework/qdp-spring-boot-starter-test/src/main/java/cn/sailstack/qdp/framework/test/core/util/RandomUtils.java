package cn.sailstack.qdp.framework.test.core.util;

import cn.sailstack.qdp.framework.common.util.date.LocalDateTimeUtils;
import cn.sailstack.qdp.framework.common.util.collection.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.common.enums.CommonStatusEnum;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 随机工具类
 *
 * @author 芋道源码
 */
public class RandomUtils {

    private static final int RANDOM_STRING_LENGTH = 10;

    private static final int TINYINT_MAX = 127;

    private static final int RANDOM_DATE_MAX = 30;

    private static final int RANDOM_COLLECTION_LENGTH = 5;

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    static {
        // 字符串
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(String.class,
                (dataProviderStrategy, attributeMetadata, map) -> randomString());
        // Integer
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(Integer.class, (dataProviderStrategy, attributeMetadata, map) -> {
            // 如果是 status 的字段，返回 0 或 1
            if ("status".equals(attributeMetadata.getAttributeName())) {
                return CommonStatusEnum.values()[java.util.concurrent.ThreadLocalRandom.current().nextInt(CommonStatusEnum.values().length)].getStatus();
            }
            // 如果是 type、status 结尾的字段，返回 tinyint 范围
            String attributeName = attributeMetadata.getAttributeName();
            if (attributeName != null && (attributeName.endsWith("type") || attributeName.endsWith("status")
                    || attributeName.endsWith("category") || attributeName.endsWith("scope") || attributeName.endsWith("result"))) {
                return java.util.concurrent.ThreadLocalRandom.current().nextInt(0, TINYINT_MAX + 1);
            }
            return java.util.concurrent.ThreadLocalRandom.current().nextInt();
        });
        // BigDecimal：限制精度在 DECIMAL(10,2) 范围内，避免 H2 等数据库溢出
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(BigDecimal.class,
                (dataProviderStrategy, attributeMetadata, map) ->
                        BigDecimal.valueOf(java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 10000000), 2));
        // LocalDateTime
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(LocalDateTime.class,
                (dataProviderStrategy, attributeMetadata, map) -> randomLocalDateTime());
        // Boolean
        PODAM_FACTORY.getStrategy().addOrReplaceTypeManufacturer(Boolean.class, (dataProviderStrategy, attributeMetadata, map) -> {
            // 如果是 deleted 的字段，返回非删除
            if ("deleted".equals(attributeMetadata.getAttributeName())) {
                return false;
            }
            return java.util.concurrent.ThreadLocalRandom.current().nextBoolean();
        });
    }

    public static String randomString() {
        return randomAlphanumeric(RANDOM_STRING_LENGTH);
    }

    public static Long randomLongId() {
        return java.util.concurrent.ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
    }

    public static Integer randomInteger() {
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }

    public static Date randomDate() {
        return Date.from(LocalDateTime.now().plusDays(java.util.concurrent.ThreadLocalRandom.current().nextLong(0, RANDOM_DATE_MAX)).atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime randomLocalDateTime() {
        // 设置 Nano 为零的原因，避免 MySQL、H2 存储不到时间戳
        return LocalDateTimeUtils.of(randomDate()).withNano(0);
    }

    public static Short randomShort() {
        return (short) java.util.concurrent.ThreadLocalRandom.current().nextInt(0, Short.MAX_VALUE);
    }

    public static <T> Set<T> randomSet(Class<T> clazz) {
        return Stream.iterate(0, i -> i).limit(java.util.concurrent.ThreadLocalRandom.current().nextInt(1, RANDOM_COLLECTION_LENGTH))
                .map(i -> randomPojo(clazz)).collect(Collectors.toSet());
    }

    public static Integer randomCommonStatus() {
        return CommonStatusEnum.values()[java.util.concurrent.ThreadLocalRandom.current().nextInt(CommonStatusEnum.values().length)].getStatus();
    }

    public static String randomEmail() {
        return randomString() + "@qq.com";
    }

    public static String randomMobile() {
        return "13800138" + randomNumbers(3);
    }

    public static String randomURL() {
        return "https://www.iocoder.cn/" + randomString();
    }

    @SafeVarargs
    public static <T> T randomPojo(Class<T> clazz, Consumer<T>... consumers) {
        T pojo = PODAM_FACTORY.manufacturePojo(clazz);
        // 非空时，回调逻辑。通过它，可以实现 Pojo 的进一步处理
        if (ArrayUtils.isNotEmpty(consumers)) {
            Arrays.stream(consumers).forEach(consumer -> consumer.accept(pojo));
        }
        return pojo;
    }

    @SafeVarargs
    public static <T> T randomPojo(Class<T> clazz, Type type, Consumer<T>... consumers) {
        T pojo = PODAM_FACTORY.manufacturePojo(clazz, type);
        // 非空时，回调逻辑。通过它，可以实现 Pojo 的进一步处理
        if (ArrayUtils.isNotEmpty(consumers)) {
            Arrays.stream(consumers).forEach(consumer -> consumer.accept(pojo));
        }
        return pojo;
    }

    @SafeVarargs
    public static <T> List<T> randomPojoList(Class<T> clazz, Consumer<T>... consumers) {
        int size = java.util.concurrent.ThreadLocalRandom.current().nextInt(1, RANDOM_COLLECTION_LENGTH);
        return randomPojoList(clazz, size, consumers);
    }

    @SafeVarargs
    public static <T> List<T> randomPojoList(Class<T> clazz, int size, Consumer<T>... consumers) {
        return Stream.iterate(0, i -> i).limit(size).map(o -> randomPojo(clazz, consumers))
                .collect(Collectors.toList());
    }


    /**
     * 生成随机字母数字字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String randomAlphanumeric(int length) {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(alphabet.charAt(java.util.concurrent.ThreadLocalRandom.current().nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    /**
     * 生成随机数字串
     *
     * @param length 长度
     * @return 随机数字串
     */
    public static String randomNumbers(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(java.util.concurrent.ThreadLocalRandom.current().nextInt(10));
        }
        return builder.toString();
    }

    /**
     * 随机获取数组中的一个元素
     *
     * @param array 数组
     * @return 随机元素
     */
    public static <T> T randomEle(T[] array) {
        return array[java.util.concurrent.ThreadLocalRandom.current().nextInt(array.length)];
    }

    /**
     * 生成 [min, max) 范围内的随机整数
     *
     * @param min 最小值（含）
     * @param max 最大值（不含）
     * @return 随机整数
     */
    public static int randomInt(int min, int max) {
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max);
    }
}
