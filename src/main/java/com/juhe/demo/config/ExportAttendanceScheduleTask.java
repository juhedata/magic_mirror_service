package com.juhe.demo.config;

import com.juhe.demo.service.IPersonalMonitorLogService;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @CLassName ExportAttendanceScheduleTask
 * @Description 导出员工考勤定时任务 ${excel.export.cron}
 * @Author kuai.zhang
 * @Date 2019/11/19 15:13
 * @Version 1.0
 **/
//@Configuration
//@EnableScheduling
@Slf4j
public class ExportAttendanceScheduleTask {

    @Value("${excel.export.save.path}")
    private String excelExportSavePath;

    @Autowired
    private IPersonalMonitorLogService iPersonalMonitorLogService;

    @Scheduled(cron = "0 30 7 1 * ?")
    private void configureTasks() {
        long t = System.currentTimeMillis();
        log.info("开始定时下载员工考勤报表..");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        try (FileOutputStream outputStream = new FileOutputStream(getExportExcelFileName(cal))) {
            XSSFWorkbook workbook = iPersonalMonitorLogService.exportAttendanceExcel(cal);
            if (workbook == null) {
                log.error("workbook为空..");
                return;
            }
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("export异常：{}", e.getMessage());
            e.printStackTrace();
        }
        log.info("定时下载员工考勤报表完成,花费时间: " + (System.currentTimeMillis() - t));
    }

    //获取导出excel文件名称
    private String getExportExcelFileName(Calendar cal) {
        String fileName = excelExportSavePath + (cal.get(Calendar.MONTH) + 1) + "月份考勤数据报表.xlsx";
        return fileName;
    }

}
