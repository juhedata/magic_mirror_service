package com.juhe.demo.bo;

import java.util.Map;
import lombok.Data;

/**
 * @CLassName ExportDateInfo
 * @Description 导出excel日期信息
 * @Author kuai.zhang
 * @Date 2019/11/18 10:29
 * @Version 1.0
 **/
@Data
public class ExportDateInfo {

    private String beginDate;

    private String endDate;

    private Integer maxDay;

    private Map<Integer, String> dayWeekMap;


}
