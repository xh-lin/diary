package com.xuhuang.diary.exceptions;

import java.util.List;

public class RegisterException extends Exception {

    private final List<String> messages;

    public RegisterException(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

}
