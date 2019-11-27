package com.juhe.demo.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @CLassName MonitorConditionAO
 * @Description 监控人脸条件AO
 * @Author kuai.zhang
 * @Date 2019/8/1 10:14
 * @Version 1.0
 **/
@ApiModel(value = "监控人脸条件AO")
@Data
public class MonitorConditionAO {

    @ApiModelProperty(value = "图片", required = true)
    @NotBlank(message = "照片不能为空")
    private String image;

    @ApiModelProperty(value = "动作", required = true)
    @NotNull(message = "动作不能为空")
    private Long action;
}