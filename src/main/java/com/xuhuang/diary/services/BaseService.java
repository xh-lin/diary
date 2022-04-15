package com.xuhuang.diary.services;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.User;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    public static final String YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS = "You do not have permission to access.";

    @Autowired
    private UserService userService;

    protected void throwIfIsNotCurrentUser(User user) throws AuthException {
        if (!userService.isCurrentUser(user)) {
            throw new AuthException(YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS);
        }
    }

}
