package com.xuhuang.diary.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.xuhuang.diary.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, Object> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return !userRepository.findByUsername((String) value).isPresent();
    }

}
