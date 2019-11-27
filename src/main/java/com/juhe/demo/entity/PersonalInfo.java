package com.juhe.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 人员信息
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PersonalInfo对象", description = "人员信息")
public class PersonalInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "性别 0->男;1->女")
    @TableField("gender")
    private Integer gender;

    @ApiModelProperty(value = "生日")
    @TableField("birthday")
    private LocalDate birthday;

    @ApiModelProperty(value = "组名")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "工号")
    @TableField("employee_no")
    private String employeeNo;

    @ApiModelProperty(value = "职称")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "部门")
    @TableField("department")
    private String department;

    @ApiModelProperty(value = "公司")
    @TableField("company")
    private String company;

    @ApiModelProperty(value = "列表显示头像")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "备注")
    @TableField("note")
    private String note;

    @ApiModelProperty(value = "头像集合")
    @TableField(exist = false)
    private List<PersonalIcon> icons;

    @ApiModelProperty(value = "mac地址集合")
    @TableField(exist = false)
    private List<PersonalMac> macs;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "是否删除 0->正常;1->删除")
    @TableField("deleted")
    private Integer deleted;
}
