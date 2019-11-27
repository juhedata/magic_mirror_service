package com.juhe.demo.component;

import com.juhe.demo.ApplicationException;
import com.juhe.demo.common.CommonResult;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @CLassName GlobalExceptionHandler
 * @Description 全局校验异常捕捉
 * @Author xxmfypp
 * @Date 2019/7/19 11:25
 * @Version 1.0
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult handleValidationException(ConstraintViolationException e) {
        for (ConstraintViolation<?> s : e.getConstraintViolations()) {
            return CommonResult.validateFailed(s.getMessage());
        }
        return CommonResult.validateFailed();
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult handleApplicationException(ApplicationException applicationException) {
        return CommonResult.validateFailed(applicationException.getMessage());
    }


}
