package com.juhe.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author zhangkuai
 * @since 2019-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value = "PersonalIcon对象")
public class PersonalIcon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "图片名称")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "人员id")
    @TableField("personal_id")
    private Long personalId;

    @ApiModelProperty(value = "特征值")
    @TableField("feature")
    private byte[] feature;

    public PersonalIcon(String icon, Long personalId, byte[] feature) {
        this.icon = icon;
        this.personalId = personalId;
        this.feature = feature;
    }
}
