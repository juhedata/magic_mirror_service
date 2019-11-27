package com.juhe.demo.common;


/**
 * @CLassName CommonResult
 * @Description 枚举了一些常用API操作码
 * @Author xxmfypp
 * @Date 2019/7/8 19:53
 * @Version 1.0
 **/
public enum ResultCodeEnum implements IErrorCode {

    SUCCESS(200, "操作成功"),
    SUCCESS_POST(201, "操作成功"),
    SUCCESS_DELETE(204, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(501, "客户端请求正常,服务端校验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "所请求的资源不存在或不可用"),
    UNSUPPORTED_MEDIA_TYPE(415, "客户端要求的返回格式不支持");

    private long code;
    private String message;

    ResultCodeEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
