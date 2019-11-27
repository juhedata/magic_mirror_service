package com.juhe.demo.ao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.group.GroupSequenceProvider;

/**
 * @CLassName AdminAO
 * @Description Admin 传入对象
 * @Author xxmfypp
 * @Date 2019/7/18 15:36
 * @Version 1.0
 **/
@Data
@ApiModel(value = "Admin 传入对象", description = "后台用户表")
@GroupSequenceProvider(value = AdminAOGroupSequenceProvider.class)
public class AdminAO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "用户密码")
    @NotBlank(message = "密码不能为空", groups = PasswordCheck.class)
    private String password;

    @ApiModelProperty(value = "邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不合法")
    private String email;

    @ApiModelProperty(value = "昵称")
    @JsonProperty("nick_name")
    private String nickName;

    @ApiModelProperty(value = "备注信息")
    private String note;

    @ApiModelProperty(value = "帐号启用状态：0->禁用；1->启用")
    private Integer status;

    @JsonProperty("role_id")
    @ApiModelProperty(value = "角色")
    private Long[] roleId;

    interface PasswordCheck {

    }

}
