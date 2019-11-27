package com.juhe.demo.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 导出excel人员监控实体详情
 * </p>
 *
 * @author zhangkuai
 * @since 2019-09-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalMonitorDetailVO implements Serializable {

    //应用名称
    @Excel(name = "日期", width = 25)
    private String date;

    //简介
    @Excel(name = "员工名称", width = 25)
    private String personName;

    //ios应用包名
    @Excel(name = "最早进入时间", width = 40)
    private String inTime;

    //android应用包名
    @Excel(name = "最晚离开时间", width = 40)
    private String outTime;

}
