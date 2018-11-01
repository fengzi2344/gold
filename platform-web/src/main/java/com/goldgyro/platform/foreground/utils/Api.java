package com.goldgyro.platform.foreground.utils;

public enum Api {
    REGISTRY("610001"),
    OPENCARDSMS("610002"),
    OPENCARD("610003"),
    QUERYSTATUS("610008"),
    SMALLPAY("610009"),
    SMALLWITHDRAW("610010"),
    BIGPAYSMS("610004"),
    BIGPAY("610005"),
    QUERYBALANCE("610012"),
    QUERYPAY("610008"),
    UPDATEFEE("610006");
    private String code;

    private Api(String code) {
        this.code = code;
    }

    public String get() {
        return this.code;
    }
}
