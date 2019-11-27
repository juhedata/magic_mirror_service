package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName PersonalStatictisAnalysisVO
 * @Description 统计分析VO
 * @Author kuai.zhang
 * @Date 2019/7/23 13:13
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "统计分析VO", description = "统计分析VO")
public class PersonalStatictisAnalysisVO {

    @ApiModelProperty(value = "人流次数")
    private Long quantity;

    @JsonProperty(value = "in_persons")
    @ApiModelProperty(value = "流入人数")
    private Integer inPersons;

    @JsonProperty(value = "out_persons")
    @ApiModelProperty(value = "流出人数")
    private Integer outPersons;

    @JsonProperty(value = "clock_in_persons")
    @ApiModelProperty(value = "打卡人数")
    private Integer clockInPersons;

    @ApiModelProperty(value = "访客人数")
    private Integer visitors;

    @JsonProperty(value = "avg_stand_time")
    @ApiModelProperty(value = "平均停留时间(分钟)")
    private Integer avgStandTime;

    @JsonProperty(value = "in_flow_rate")
    @ApiModelProperty(value = "实时流入速率")
    private Integer inflowRate;

    @JsonProperty(value = "out_flow_rate")
    @ApiModelProperty(value = "实时流出速率")
    private Integer outflowRate;

    @JsonProperty(value = "discrete_flow_nums")
    @ApiModelProperty(value = "人流次数离散对象")
    private List<DiscreteFlowNumVO> discreteFlowNums;

}