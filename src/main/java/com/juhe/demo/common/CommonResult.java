package com.juhe.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.juhe.demo.vo.NullVO;
import lombok.Data;

/**
 * @CLassName CommonResult
 * @Description 通用的返回结果
 * @Author xxmfypp
 * @Date 2019/7/8 19:53
 * @Version 1.0
 **/
@Data
public class CommonResult<T> {

    private long code;
    private String message;
    private T data;
    @JsonInclude(Include.NON_EMPTY)
    private String token;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message) {
        this.code = code;
        this.message = message;
    }

    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(),
            (T) dealNull(data));
    }

    /**
     * 成功返回结果
     */
    public static CommonResult success() {
        return new CommonResult(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), message, (T) dealNull(data));
    }


    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> successPost(T data) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS_POST.getCode(), ResultCodeEnum.SUCCESS.getMessage(),
            (T) dealNull(data));
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param message 提示信息
     */
    public static <T> CommonResult<T> successPost(T data, String message) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS_POST.getCode(), message, (T) dealNull(data));
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> successDelete(T data) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS_DELETE.getCode(), ResultCodeEnum.SUCCESS.getMessage(),
            (T) dealNull(data));
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param message 提示信息
     */
    public static <T> CommonResult<T> successDelete(T data, String message) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS_DELETE.getCode(), message, (T) dealNull(data));
    }


    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), (T) dealNull(null));
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCodeEnum.FAILED.getCode(), message, (T) dealNull(null));
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCodeEnum.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCodeEnum.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCodeEnum.VALIDATE_FAILED.getCode(), message, (T) dealNull(null));
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage(),
            (T) dealNull(data));
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage(),
            (T) dealNull(data));
    }

    private static Object dealNull(Object data) {
        return (data == null) ? new NullVO() : data;
    }

}
