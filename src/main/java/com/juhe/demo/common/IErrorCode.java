package com.juhe.demo.common;

/**
 * @CLassName IErrorCode
 * @Description 封装API的错误码
 * @Author xxmfypp
 * @Date 2019/7/8 19:54
 * @Version 1.0
 **/
public interface IErrorCode {

    /**
      * @Author xxmfypp
      * @Description 获取错误编码
      * @Date 19:57 2019/7/8
      * @param
      * @return long
      **/
    long getCode();

    /**
      * @Author xxmfypp
      * @Description 获取错误信息
      * @Date 19:57 2019/7/8
      * @param
      * @return java.lang.String
      **/
    String getMessage();

}
