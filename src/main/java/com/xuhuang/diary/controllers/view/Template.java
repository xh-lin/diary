package com.xuhuang.diary.controllers.view;

public enum Template {

    LOGIN("login"),
    REGISTER("register"),
    ERROR("error"),
    DIARY("diary");

    private String name;

    Template(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
