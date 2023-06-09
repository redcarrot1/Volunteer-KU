package kr.ac.konkuk.demo.global.validation.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.ac.konkuk.demo.global.validation.annotation.ValidEnum;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private ValidEnum annotation;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Object[] enumValues = this.annotation.enumClass().getEnumConstants();

        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                // 대소문자 구분하지 않기
                if (value.equalsIgnoreCase(enumValue.toString())) {
                    return true;
                }
            }
        }

        return false;
    }
}
