package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luxianzhu
 * @version 1.0
 * @description Admin 传入对象
 * @date 2019/8/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminVO {

    private Long id;

    private String username;

    private String password;

    private String email;

    @JsonProperty("nick_name")
    private String nickName;

    private String note;

    private Integer status;

    @JsonProperty(value = "role_id")
    private Long[] roleId;

    @JsonProperty(value = "role_name")
    private String[] roleName;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonProperty("login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;

}
