package com.juhe.demo.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @CLassName LoginAO
 * @Description 管理员信息AO
 * @Author xuman.xu
 * @Date 2019/7/17 11:04
 * @Version 1.0
 **/
@Data
@ApiModel(value = "登录信息", description = "传入的登录信息")
public class LoginAO {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

}