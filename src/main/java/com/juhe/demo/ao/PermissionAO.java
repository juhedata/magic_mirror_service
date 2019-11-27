package com.juhe.demo.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @CLassName PermissionAO
 * @Description 权限传入对象
 * @Author xxmfypp
 * @Date 2019/7/18 16:35
 * @Version 1.0
 **/
@Data
@ApiModel(value = "权限传入对象", description = "权限信息")
public class PermissionAO {

    @ApiModelProperty(value = "权限id")
    private Long id;

    @ApiModelProperty(value = "父级权限id")
    private Long pid;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空")
    @Length(max = 30, message = "名称长度最多为30")
    private String name;

    @ApiModelProperty(value = "权限值", required = true)
    @NotBlank(message = "权限值不能为空")
    @Length(max = 30, message = "权限值长度最多为30")
    private String value;

    @ApiModelProperty(value = "图标")
    @Length(max = 100, message = "图标名称长度最多为100")
    private String icon;

    @ApiModelProperty(value = "权限类型：0->目录；1->菜单；2->按钮（接口绑定权限）", required = true)
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 2, message = "状态只能为0或1或2")
    private Integer type;

    @ApiModelProperty(value = "资源路径", required = true)
    @NotBlank(message = "资源路径长度不能为空")
    @Length(max = 100, message = "资源路径长度最多为100")
    private String uri;

    @ApiModelProperty(value = "启用状态；0->禁用；1->启用")
    private Integer status;

    @ApiModelProperty(value = "排序")
    @Digits(integer = 10000, fraction = 1, message = "排序只能为1-10000的数字")
    private Integer sort;

}
