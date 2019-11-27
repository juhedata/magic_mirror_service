package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author zhangkuai
 * @since 2019-07-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalIconVO {

    private Long id;

    private String icon;

    @JsonProperty(value = "personal_id")
    private Long personalId;

    public PersonalIconVO(String icon, Long personalId) {
        this.icon = icon;
        this.personalId = personalId;
    }
}
