package com.juhe.demo.ao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @CLassName RoleAO
 * @Description 角色传入对象
 * @Author xxmfypp
 * @Date 2019/7/18 11:23
 * @Version 1.0
 **/
@Data
@ApiModel(value = "角色传入对象", description = "角色信息")
public class RoleAO {

    @ApiModelProperty(value = "角色主键")
    private Long id;

    @ApiModelProperty(value = "角色编号", required = true)
    @NotBlank(message = "角色编码不能为空")
    @Length(max = 30, message = "角色编码长度最多为30")
    private String code;

    @ApiModelProperty(value = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    @Length(max = 30, message = "角色长度最多为30")
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 100, message = "描述长度不能超过100")
    private String description;

    @ApiModelProperty(value = "启用状态：0->禁用；1->启用", required = true)
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    private Integer status;

    @ApiModelProperty(value = "排序")
    @Digits(integer = 10000, fraction = 1, message = "排序只能为1-10000的数字")
    private Integer sort;

    @ApiModelProperty(value = "权限Id", required = true)
    @NotNull(message = "权限不可为空")
    @JsonProperty(value = "permission_id")
    private Long[] permissionId;

}
