package com.xuhuang.diary.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.spel.standard.SpelExpressionParser;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private String[] fieldNames;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        fieldNames = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (fieldNames.length <= 1)
            return true;

        Object o1 = parser.parseExpression(fieldNames[0]).getValue(value);

        for (int i = 1; i < fieldNames.length; i++) {
            Object o2 = parser.parseExpression(fieldNames[i]).getValue(value);

            if (o1 == null) {
                if (o2 != null)
                    return false;
            } else if (!o1.equals(o2)) {
                return false;
            }
        }

        return true;
    }

}
