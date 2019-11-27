package com.juhe.demo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 角色VO
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleVO {

    private Long id;

    private String code;

    private String name;

    private String description;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "create_time")
    private LocalDateTime createTime;

    @TableField("status")
    private Integer status;

    private Integer sort;

    @JsonProperty(value = "permission_id")
    private Long[] permissionId;

    @JsonProperty(value = "permission_name")
    private String[] permissionName;


}
