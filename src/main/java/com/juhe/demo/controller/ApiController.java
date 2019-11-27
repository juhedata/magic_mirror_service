package com.juhe.demo.controller;


import com.juhe.demo.common.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xuxm
 * @since 2019-07-01
 */
@RestController
@RequestMapping(value = "/")
@Api(value = "百度人体识别相关API", tags = "百度人体识别相关API")
@Slf4j
@Validated
public class ApiController {

    @ApiOperation(value = "健康检查")
    @GetMapping(path = "health")
    public CommonResult<String> healthCheck() {
        return CommonResult.success("success");
    }
}
