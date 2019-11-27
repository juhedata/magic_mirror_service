package com.juhe.demo.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName PersonAttendance
 * @Description 人员考勤信息
 * @Author kuai.zhang
 * @Date 2019/11/18 15:18
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
public class PersonAttendance {

    private String date;

    private String earliestTime;

    private String latestTime;

    //加班时长
    private String overTime;

    //出勤时间
    private String attendanceTime;

    private boolean red = false;

    public PersonAttendance(String date, String earliestTime, String latestTime, String overTime, String attendanceTime) {
        this.date = date;
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.overTime = overTime;
        this.attendanceTime = attendanceTime;
    }
}
