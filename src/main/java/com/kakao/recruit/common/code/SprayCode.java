package com.kakao.recruit.common.code;

import java.util.Arrays;

public enum SprayCode {
    SUCCESS("SUCCESS", 1000),
    SPRAY_NOT_EXISTS("SPRAY_NOT_EXISTS", 2000),
    NOT_PARTICIPANT("NOT_PARTICIPANT", 3000),
    ALREADY_ALLOCATED("ALREADY_ALLOCATED", 4000),
    REQUEST_BY_SELF("REQUEST_BY_SELF", 5001),
    EXPIRED_REQUEST("EXPIRED_REQUEST", 5002),
    INVALID_REQUEST("INVALID_REQUEST", 5003);

    private final String name;
    private final int code;

    SprayCode(String name, int code){
        this.name = name;
        this.code= code;
    }

    public String getName(){return this.name;}

    public int getValue(){return this.code;}
}
