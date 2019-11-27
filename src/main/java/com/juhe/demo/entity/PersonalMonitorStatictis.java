package com.juhe.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 人员监控统计信息
 * </p>
 *
 * @author xuxm
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "PersonalMonitorStatictis对象", description = "人员监控统计信息")
public class PersonalMonitorStatictis implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "人员编号")
    @TableField("personal_id")
    private Long personalId;

    @ApiModelProperty(value = "出次数")
    @TableField("out_count")
    private Integer outCount;

    @ApiModelProperty(value = "入次数")
    @TableField("in_count")
    private Integer inCount;

    @ApiModelProperty(value = "停留时间")
    @TableField("stand_time")
    private Long standTime;

    @ApiModelProperty(value = "最早进入时间")
    @TableField("earliest_time")
    private LocalTime earliestTime;

    @ApiModelProperty(value = "最晚出去时间")
    @TableField("latest_time")
    private LocalTime latestTime;

    @ApiModelProperty(value = "日期")
    @TableField("statistics_time")
    private LocalDate statisticsTime;

    public PersonalMonitorStatictis(Long personalId, Integer outCount, Integer inCount, Long standTime,
        LocalTime earliestTime, LocalTime latestTime, LocalDate statisticsTime) {
        this.personalId = personalId;
        this.outCount = outCount;
        this.inCount = inCount;
        this.standTime = standTime;
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.statisticsTime = statisticsTime;
    }
}
