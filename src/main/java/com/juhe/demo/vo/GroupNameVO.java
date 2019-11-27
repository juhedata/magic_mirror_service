package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xianzhu.lu
 * @description 组名称
 * @date 2019-8-1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupNameVO {

    private Long id;

    private String name;

    @JsonProperty("create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Integer deleteable;


}
