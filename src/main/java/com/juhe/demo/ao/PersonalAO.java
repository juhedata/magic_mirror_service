package com.juhe.demo.ao;

import com.juhe.demo.entity.PersonalIcon;
import com.juhe.demo.entity.PersonalMac;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName PersonalAO
 * @Description 个人信息 AO
 * @Author xxmfypp
 * @Date 2019/7/11 15:10
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@ApiModel(value = "PersonalInfo对象", description = "人员信息")
public class PersonalAO {

    private Long id;

    @ApiModelProperty(value = "姓名")
    private String name;

    private Long groupId;

    @ApiModelProperty(value = "组显示名称")
    private String groupName;

    @ApiModelProperty(value = "性别 0->男;1->女")
    private Integer gender;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "工号")
    private String employeeNo;

    @ApiModelProperty(value = "职称")
    private String title;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "公司")
    private String company;

    @ApiModelProperty(value = "头像")
    private String icon;

    @ApiModelProperty(value = "mac地址")
    private String mac;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "头像集合")
    private List<PersonalIcon> icons;

    @ApiModelProperty(value = "mac集合")
    private List<PersonalMac> macs;

    @ApiModelProperty(value = "是否删除 0->正常;1->删除")
    private Integer deleted;

    public PersonalAO(Long id, String name, Long groupId, Integer gender, String birthday, String employeeNo,
        String title, String department, String company, String note) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.gender = gender;
        this.birthday = birthday;
        this.employeeNo = employeeNo;
        this.title = title;
        this.department = department;
        this.company = company;
        this.note = note;
    }
}
