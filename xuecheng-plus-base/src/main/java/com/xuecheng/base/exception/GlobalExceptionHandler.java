package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
    自定义异常
    * */
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //返回状态码
    public RestErrorResponse customException(XueChengPlusException e) {
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());

    }

    /*
    * 非自定义异常
    * */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> errors=new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item->{
            errors.add(item.getDefaultMessage());
        });

        String errorMessage = StringUtils.join(errors, ",");

        log.error("系统异常{}",e.getMessage(),errorMessage);
        RestErrorResponse restErrorResponse=new RestErrorResponse(errorMessage);
        return restErrorResponse;
    }

//    @ResponseBody
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public RestErrorResponse exception1(Exception e) {
//
//        log.error("【系统异常】{}",e.getMessage(),e);
//        e.printStackTrace();
//        if(e.getMessage().equals("不允许访问")){
//            return new RestErrorResponse("没有操作此功能的权限");
//        }
//        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
//
//
//    }

}
