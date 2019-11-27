package com.juhe.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 人员监控信息
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "PersonalMonitorLog对象", description = "人员监控信息")
public class PersonalMonitorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "人员编号")
    @TableField("personal_id")
    private Long personalId;

    @ApiModelProperty(value = "动作 0->入；1->出")
    @TableField("action")
    private Integer action;

    @ApiModelProperty(value = "来源 0:人脸识别,1:mac识别")
    @TableField("source")
    private Integer source;

    @ApiModelProperty(value = "监控时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    public PersonalMonitorLog(Long personalId, Integer action, Integer source, LocalDateTime createTime) {
        this.personalId = personalId;
        this.action = action;
        this.source = source;
        this.createTime = createTime;
    }
}
