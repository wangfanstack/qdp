package cn.sailstack.qdp.framework.mybatis.core.type;

import com.google.common.base.Preconditions;
import com.google.common.base.Preconditions;
import cn.sailstack.qdp.framework.common.util.string.AesUtils;
import cn.sailstack.qdp.framework.common.util.spring.SpringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字段字段的 TypeHandler 实现类，基于 {@link AES} 实现
 * 可通过 jasypt.encryptor.password 配置项，设置密钥
 *
 * @author 芋道源码
 */
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    private static final String ENCRYPTOR_PROPERTY_NAME = "mybatis-plus.encryptor.password";


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decrypt(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decrypt(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decrypt(value);
    }

    private static String decrypt(String value) {
        if (value == null) {
            return null;
        }
        return AesUtils.decrypt(value, getEncryptKey());
    }

    public static String encrypt(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        return AesUtils.encrypt(rawValue, getEncryptKey());
    }

    private static String getEncryptKey() {
        String password = SpringUtils.getProperty(ENCRYPTOR_PROPERTY_NAME);
        Preconditions.checkArgument(password != null && !password.isBlank(), "配置项 %s 不能为空", ENCRYPTOR_PROPERTY_NAME);
        return password;
    }

}
