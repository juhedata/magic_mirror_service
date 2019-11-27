package com.juhe.demo.bo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName StatisticPersonAttendance
 * @Description 员工考勤统计信息
 * @Author kuai.zhang
 * @Date 2019/11/18 17:38
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticPersonAttendance {

    private Long personId;

    private List<PersonAttendance> attendances;

    //工作日总加班时间
    private String weekDayTotalOverTime;

    //工作日总出勤时间
    private String weekDayTotalAttendanceTime;
}
