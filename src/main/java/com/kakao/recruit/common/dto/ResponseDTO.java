package com.kakao.recruit.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseDTO {
    private int status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result;

    ResponseDTO(){}
    public ResponseDTO(int status, Object result){
        this.status = status;
        this.result = result;
    }

    public ResponseDTO(int status,String message){
        this.status = status;
        this.message = message;
    }
    public ResponseDTO(int status, String message, Object result){
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
