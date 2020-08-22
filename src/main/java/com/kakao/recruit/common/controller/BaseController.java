package com.kakao.recruit.common.controller;

import com.kakao.recruit.common.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


public class BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ResponseDTO sendResponse(int status, Object result){
        return new ResponseDTO(status, result);
    }
    protected ResponseDTO sendResponse(int status, String message){
        return new ResponseDTO(status, message);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDTO handleDefaultErrorResponse(Exception exception){
        exception.printStackTrace();
        return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }
}
