package com.xuhuang.diary.controllers;

public enum Template {

    INDEX("index"),
    LOGIN("login"),
    REGISTER("register"),
    ERROR("error");

    private String name;

    Template(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
