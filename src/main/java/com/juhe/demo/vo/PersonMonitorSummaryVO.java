package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName PersonMonitorSummaryVO
 * @Description 人员监控统计信息
 * @Author xxmfypp
 * @Date 2019/7/16 16:37
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "监控事件统计", description = "监控事件统计")
public class PersonMonitorSummaryVO implements Serializable {

    @JsonProperty(value = "in_num")
    @ApiModelProperty(value = "进入次数")
    private Integer inNum;

    @JsonProperty(value = "out_num")
    @ApiModelProperty(value = "离开次数")
    private Integer outNum;

    @JsonProperty(value = "stand_time")
    @ApiModelProperty(value = "停留时间(分钟)")
    private Long standTime;

}
