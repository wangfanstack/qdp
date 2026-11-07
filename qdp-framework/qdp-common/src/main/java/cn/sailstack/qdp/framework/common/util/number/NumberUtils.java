package cn.sailstack.qdp.framework.common.util.number;

import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 数字的工具类，补全 {@link NumberUtil} 的功能
 *
 * @author 芋道源码
 */
public class NumberUtils {

    private static final Pattern FIRST_BIG_DECIMAL_PATTERN = Pattern.compile("-?\\d+(?:\\.\\d+)?");

    /**
     * 一百，用于百分数和比例计算
     */
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public static Long parseLong(String str) {
        return StringUtils.isNotEmpty(str) ? Long.valueOf(str) : null;
    }

    public static Integer parseInt(String str) {
        return StringUtils.isNotEmpty(str) ? Integer.valueOf(str) : null;
    }

    /**
     * 获得数字，为 null 时返回 {@link BigDecimal#ZERO}
     *
     * @param value 数字
     * @return 数字
     */
    public static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 从文本中解析首个数字
     *
     * @param text 文本
     * @return 数字；不存在时返回 {@link BigDecimal#ZERO}
     */
    public static BigDecimal parseFirstBigDecimal(String text) {
        if (StringUtils.isEmpty(text)) {
            return BigDecimal.ZERO;
        }
        java.util.regex.Matcher matcher = FIRST_BIG_DECIMAL_PATTERN.matcher(text.replace(",", ""));
        String number = matcher.find() ? matcher.group() : null;
        return toBigDecimal(number);
    }

    public static boolean isAllNumber(List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return false;
        }
        for (String value : values) {
            if (!isNumber(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通过经纬度获取地球上两点之间的距离
     *
     * 参考 <<a href="https://gitee.com/dromara/hutool/blob/1caabb586b1f95aec66a21d039c5695df5e0f4c1/hutool-core/src/main/java/cn/hutool/core/util/DistanceUtil.java">DistanceUtil</a>> 实现，目前它已经被 hutool 删除
     *
     * @param lat1 经度1
     * @param lng1 纬度1
     * @param lat2 经度2
     * @param lng2 纬度2
     * @return 距离，单位：千米
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = lat1 * Math.PI / 180.0;
        double radLat2 = lat2 * Math.PI / 180.0;
        double a = radLat1 - radLat2;
        double b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        distance = distance * 6378.137;
        distance = Math.round(distance * 10000d) / 10000d;
        return distance;
    }

    /**
     * 提供精确的乘法运算
     *
     * 和 hutool {@link NumberUtil#mul(BigDecimal...)} 的差别是，如果存在 null，则返回 null
     *
     * @param values 多个被乘值
     * @return 积
     */
    public static BigDecimal mul(BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value == null) {
                return null;
            }
        }
        return mul(values);
    }


    /**
     * 把给定值转为 {@link BigDecimal}；null 传入返回 null，字符串解析失败抛出 {@link NumberFormatException}
     *
     * @param value 值
     * @return BigDecimal
     */
    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }


    /**
     * 判断字符串是否为数字（整数或小数，支持正负号）
     *
     * @param str 字符串
     * @return 是否数字
     */
    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 多个数相乘，返回以第一个数精度的乘积
     *
     * @param values 数值
     * @return 乘积
     */
    public static BigDecimal mul(Number... values) {
        if (values == null || values.length == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = toBigDecimal(values[0]);
        for (int i = 1; i < values.length; i++) {
            result = result.multiply(toBigDecimal(values[i]));
        }
        return result;
    }

    /**
     * 将对象转换为指定数值类型；支持 Integer/Long/Double/Float/BigDecimal/Short/Byte
     *
     * @param clazz 目标类型
     * @param value 值
     * @param <T>   目标类型
     * @return 转换结果；null 传入返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Class<T> clazz, Object value) {
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        BigDecimal decimal;
        if (value instanceof BigDecimal bd) {
            decimal = bd;
        } else if (value instanceof Number number) {
            decimal = BigDecimal.valueOf(number.doubleValue());
        } else {
            decimal = new BigDecimal(value.toString().trim());
        }
        if (clazz == Integer.class) {
            return (T) Integer.valueOf(decimal.intValue());
        }
        if (clazz == Long.class) {
            return (T) Long.valueOf(decimal.longValue());
        }
        if (clazz == Double.class) {
            return (T) Double.valueOf(decimal.doubleValue());
        }
        if (clazz == Float.class) {
            return (T) Float.valueOf(decimal.floatValue());
        }
        if (clazz == Short.class) {
            return (T) Short.valueOf(decimal.shortValue());
        }
        if (clazz == Byte.class) {
            return (T) Byte.valueOf(decimal.byteValue());
        }
        if (clazz == BigDecimal.class) {
            return (T) decimal;
        }
        if (clazz == String.class) {
            return (T) decimal.toPlainString();
        }
        throw new IllegalArgumentException("不支持的数值类型：" + clazz.getName());
    }

    /**
     * 判断字符串是否为整数
     *
     * @param str 字符串
     * @return 是否整数
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 字符串转整数，失败返回默认值
     *
     * @param str          字符串
     * @param defaultValue 默认值
     * @return 整数
     */
    public static Integer toInt(String str, Integer defaultValue) {
        if (str == null || str.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 两数相除（整数除法），除数为 0 时返回 0
     *
     * @param num1 被除数
     * @param num2 除数
     * @return 商
     */
    public static int div(int num1, int num2) {
        return num2 == 0 ? 0 : num1 / num2;
    }

    /**
     * 两数相除，结果保留整数部分（对 Double/Long 兼容）；除数为 0 时返回 0
     *
     * @param num1 被除数
     * @param num2 除数
     * @return 商
     */
    public static long div(Number num1, Number num2) {
        return num2 == null || num2.longValue() == 0 ? 0 : num1.longValue() / num2.longValue();
    }
}
