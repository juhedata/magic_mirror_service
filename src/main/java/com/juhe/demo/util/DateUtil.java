package com.juhe.demo.util;

import java.text.DateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @CLassName DateUtil
 * @Description 日期时间工具类
 * @Author kuai.zhang
 * @Date 2019/7/23 11:12
 * @Version 1.0
 **/
@Slf4j
public class DateUtil {

    public static boolean isValidDateTime(String startTime, String endTime, DateFormat formatter) {
        boolean rst = false;
        boolean existOneNotEmpty =
            (StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)) || (StringUtils.isBlank(endTime)
                && StringUtils.isNotBlank(startTime));
        if (existOneNotEmpty) {
            return rst;
        }
        try {
            Date start = formatter.parse(startTime);
            Date end = formatter.parse(endTime);
            rst = !start.after(end);
        } catch (Exception e) {
            log.error("parse datetime failed.");
        }
        return rst;
    }


}