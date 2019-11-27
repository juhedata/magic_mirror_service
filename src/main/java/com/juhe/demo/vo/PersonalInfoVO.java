package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 人员信息VO
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoVO {

    private Long id;

    private String name;

    private Integer gender;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @JsonProperty(value = "group_id")
    private Long groupId;

    @JsonProperty(value = "employee_no")
    private String employeeNo;

    private String title;

    private String department;

    private String company;

    private String icon;

    private String note;

    private List<PersonalIconVO> icons;

    private String mac;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Integer deleted;

}
