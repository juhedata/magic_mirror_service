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
 * @Description 监控大屏VO
 * @Author kuai.zhang
 * @Date 2019/7/23 13:13
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "监控大屏VO", description = "监控大屏VO")
public class PersonalMonitorScreenVO {

    @JsonProperty(value = "today_quantity")
    @ApiModelProperty(value = "今日人流次数")
    private Long todayQuantity;

    @JsonProperty(value = "nearly_seven_days_quantity")
    @ApiModelProperty(value = "近7天人流次数")
    private Long nearlySevenDaysQuantity;

    @JsonProperty(value = "today_in_persons")
    @ApiModelProperty(value = "今日流入人数")
    private Integer todayInPersons;

    @JsonProperty(value = "today_out_persons")
    @ApiModelProperty(value = "今日流出人数")
    private Integer todayOutPersons;

    @JsonProperty(value = "nearly_seven_days_in_persons")
    @ApiModelProperty(value = "近7天流入人数")
    private Integer nearlySevenDaysInPersons;

    @JsonProperty(value = "nearly_seven_days_out_persons")
    @ApiModelProperty(value = "近7天流出人数")
    private Integer nearlySevenDaysOutPersons;

    @JsonProperty(value = "clock_in_persons")
    @ApiModelProperty(value = "今日打卡人数")
    private Integer clockInPersons;

    @ApiModelProperty(value = "今日访客人数")
    private Integer visitors;

    @JsonProperty(value = "in_flow_rate")
    @ApiModelProperty(value = "实时流入速率")
    private Integer inflowRate;

    @JsonProperty(value = "out_flow_rate")
    @ApiModelProperty(value = "实时流出速率")
    private Integer outflowRate;

    @JsonProperty(value = "discrete_flow_monitor_bos")
    @ApiModelProperty(value = "人流监控离散对象")
    private List<DiscreteFlowMonitorVO> discreteFlowMonitorVOS;

    public PersonalMonitorScreenVO(Long todayQuantity, Long nearlySevenDaysQuantity, Integer todayInPersons,
        Integer todayOutPersons, Integer nearlySevenDaysInPersons, Integer nearlySevenDaysOutPersons,
        Integer clockInPersons, Integer visitors, Integer inflowRate, Integer outflowRate) {
        this.todayQuantity = todayQuantity;
        this.nearlySevenDaysQuantity = nearlySevenDaysQuantity;
        this.todayInPersons = todayInPersons;
        this.todayOutPersons = todayOutPersons;
        this.nearlySevenDaysInPersons = nearlySevenDaysInPersons;
        this.nearlySevenDaysOutPersons = nearlySevenDaysOutPersons;
        this.clockInPersons = clockInPersons;
        this.visitors = visitors;
        this.inflowRate = inflowRate;
        this.outflowRate = outflowRate;
    }

}