package cn.sailstack.qdp.framework.common.validation;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelephoneValidator implements ConstraintValidator<Telephone, String> {

    @Override
    public void initialize(Telephone annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果手机号为空，默认不校验，即校验通过
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        // 校验手机
        return value != null && (value.matches("0\\d{2,3}-\\d{7,8}(-\\d{1,4})?") // 座机
                || value.matches("1[3-9]\\d{9}")); // 手机号
    }

}
