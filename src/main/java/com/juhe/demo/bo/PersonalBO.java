package com.juhe.demo.bo;

import com.juhe.demo.entity.PersonalInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @CLassName PersonalBO
 * @Description 个人信息 BO
 * @Author xxmfypp
 * @Date 2019/7/12 17:19
 * @Version 1.0
 **/
@Data
@ApiModel(value="PersonalInfo BO", description="人员信息")
public class PersonalBO extends PersonalInfo {

    @ApiModelProperty(value = "情感")
    private String emotion;

}
