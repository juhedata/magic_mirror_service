package com.juhe.demo.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @CLassName SystemConstant
 * @Description 公共常量类
 * @Author xxmfypp
 * @Date 2019/7/11 15:36
 * @Version 1.0
 **/
public interface SystemConstant {

    interface Common {

        //分隔符
        String DELIMITER = ",";
        //mac验证正则
        String MAC_VALID = "([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}";
        //URL编码
        String URL_ENCODE = "gbk";
    }

    interface PermitRequest {

        String[] MATCH_PERMIT_PATH = new String[]{"/login", "/logout", "/analyze/**", "/health", "/export/**", "/statistics/screen"};
    }

    interface PoolProperties {

        //初始化连接数
        int initialSize = 10;
        //最大空闲
        int maxIdle = 10;
        //最大总数
        int maxTotal = 20;
        //最小空闲
        int minIdle = 6;
    }

    interface Personal {

        int UNDELETE = 0;
        String TEMPLATE_NAME = "人员录入模板.xlsx";
        List<String> IMAGE_TYPES = new ArrayList<>(Arrays.asList("jpg", "png", "jpeg"));

        String EXCEL_XLS = "xls";
        String EXCEL_XLSX = "xlsx";
        String IMAGE_PATH = "/analyze/download/";
        String IMAGE_COMPRESS = "_compress";
        double IMAGE_SCALE = 1d;
        double IMAGE_QUALITY = 0.25d;
        int FETCH_SIZE = 300;
    }

    interface GroupName {

        int CANDELETE = 0;
        String DEFAULT_GROUP_NAME = "聚合数据";
    }

    interface PersonalMonitor {

        int YESTERDAY = -1;
        int TODAY = 0;
        int NEARLY_SEVEN_DAYS = 1;
        int NEARLY_THIRTY_DAYS = 2;
        int MAX_DAYS = 31;
        String ZERO_HOUR_SUFFIX = " 00:00:00";
        String LAST_HOUR_SUFFIX = " 23:59:59";
        DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
        String[] WEEK_DAYS = {"日", "一", "二", "三", "四", "五", "六"};
        String EARLIEST_STR = "最早";
        String LATEST_STR = "最晚";
        String OVERTIME_STR = "加班时长";
        String ATTENDANCE_STR = "出勤时间";
        String HOUR_STR = "小时";
        String MINUTE_STR = "分钟";
        String FONT_NAME = "微软雅黑";
    }

    interface RedisKeyPrefix {

        String MONITORIN = "monitorin_";
        String MONITOROUT = "monitorout_";
        String MONITORPROBE = "monitorprobe_";
    }

    interface BackResult {

        String SUCCESS = "SUCCESS";
    }

    interface DateFormatInstant {

        DateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat simpleDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd[['T'hh][:mm][:ss]]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
            .toFormatter();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

}
