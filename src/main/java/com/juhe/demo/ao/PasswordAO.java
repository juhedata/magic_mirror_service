package com.juhe.demo.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luxianzhu
 * @date 2019/08/07
 * @description 修改用户密码接收参数参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "修改密码实体传参", description = "")
public class PasswordAO {

    @ApiModelProperty(value = "原密码", required = true)
    @NotBlank(message = "原密码不可为空")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不可为空")
    private String newPassword;

    @ApiModelProperty(value = "确认密码", required = true)
    @NotBlank(message = "确认密码不可为空")
    private String confirmPassword;

}
