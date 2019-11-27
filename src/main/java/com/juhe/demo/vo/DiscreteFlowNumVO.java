package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName DiscreteFlowNumVO
 * @Description 人流次数离散对象
 * @Author kuai.zhang
 * @Date 2019/7/23 13:53
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "人流次数离散VO", description = "人流次数离散VO")
public class DiscreteFlowNumVO {

    @ApiModelProperty(value = "时间")
    private String time;

    @JsonProperty(value = "in_num")
    @ApiModelProperty(value = "流入次数")
    private Integer inNum;

    @JsonProperty(value = "out_num")
    @ApiModelProperty(value = "流出次数")
    private Integer outNum;

}