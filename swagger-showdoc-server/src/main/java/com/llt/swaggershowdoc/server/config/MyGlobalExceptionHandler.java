package com.llt.swaggershowdoc.server.config;

import com.llt.swaggershowdoc.server.models.BaseResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class MyGlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse customException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return BaseResponse.error(bindingResult.getAllErrors().get(0).getDefaultMessage());
    }


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public BaseResponse customException(Exception e) {
        e.printStackTrace();
        return BaseResponse.error(e.getMessage());
    }
}
