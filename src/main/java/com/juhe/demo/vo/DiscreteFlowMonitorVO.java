package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName DiscreteFlowNumVO
 * @Description 监控大屏人流离散对象
 * @Author kuai.zhang
 * @Date 2019/7/23 13:53
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "监控大屏人流离散VO", description = "监控大屏人流离散VO")
public class DiscreteFlowMonitorVO {

    @ApiModelProperty(value = "时间")
    private Long time;

    @JsonProperty(value = "personal_id")
    @ApiModelProperty(value = "人员id")
    private Long personalId;

    @ApiModelProperty(value = "进/出")
    private Integer action;

}