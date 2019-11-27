package com.juhe.demo.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName PersonMonitorConditionBO
 * @Description 人员监控信息条件
 * @Author xxmfypp
 * @Date 2019/7/16 15:30
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonMonitorConditionBO {
    private String name;

    private String startTime;

    private String endTime;

    private Integer action;

    private Long groupId;

}
