package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName PersonMonitorInfoVO
 * @Description 人员监控信息VO
 * @Author xxmfypp
 * @Date 2019/7/16 15:23
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "人员监控信息", description = "人员监控信息")
public class PersonMonitorInfoVO implements Serializable {

    private Long id;

    @JsonProperty(value = "personal_id")
    @ApiModelProperty(value = "人员id")
    private Long personalId;

    @JsonProperty(value = "personal_name")
    @ApiModelProperty(value = "人员名称")
    private String personalName;

    @JsonProperty(value = "monitor_time")
    @ApiModelProperty(value = "监控时间")
    private String monitorTime;

    @ApiModelProperty(value = "动作(进/出)")
    private Integer action;

    @JsonProperty(value = "group_name")
    @ApiModelProperty(value = "组名称")
    private String groupName;

}
