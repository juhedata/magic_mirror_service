package com.juhe.demo.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant.DateFormatInstant;
import com.juhe.demo.constant.SystemConstant.PersonalMonitor;
import com.juhe.demo.entity.PersonalInfo;
import com.juhe.demo.entity.PersonalMonitorLog;
import com.juhe.demo.service.IPersonalInfoService;
import com.juhe.demo.service.IPersonalMonitorLogService;
import com.juhe.demo.util.DateUtil;
import com.juhe.demo.vo.PersonMonitorSummaryVO;
import com.juhe.demo.vo.PersonalMonitorDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 人员监控信息 前端控制器
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Api(value = "人员监控相关API", tags = "人员监控相关API")
@RestController
@RequestMapping("/")
@Slf4j
@Validated
public class PersonalMonitorLogController {

    @Autowired
    private IPersonalMonitorLogService iPersonalMonitorLogService;

    @Autowired
    private IPersonalInfoService iPersonalInfoService;

    @ApiOperation(value = "人员监控信息列表", notes = "分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "姓名", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "start_time", value = "监控起始时间", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "end_time", value = "监控结束时间", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "action", value = "进入/离开", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "group_id", value = "分组编号", dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "page", required = true, value = "第几页", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "size", required = true, value = "每页条数", dataType = "int", paramType = "query")
    })
    @GetMapping(value = "logs")
    @PreAuthorize("hasAuthority('monitor')")
    public CommonResult listPersonMonitorInfo(@RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "start_time", required = false) String startTime,
        @RequestParam(value = "end_time", required = false) String endTime,
        @RequestParam(value = "action", required = false) Integer action,
        @RequestParam(value = "group_id", required = false) Long groupId,
        @NotNull(message = "页数不能为空") @RequestParam(value = "page", defaultValue = "1") Integer page,
        @NotNull(message = "每页条数不能为空") @RequestParam(value = "size", defaultValue = "10") Integer size) {
        CommonResult result;

        if (StringUtils.isAllBlank(startTime, endTime)) {
            result = iPersonalMonitorLogService
                .listPersonMonitorInfo(name, startTime, endTime, action, groupId, page, size);
        } else {
            boolean existOneNotEmpty =
                (StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)) || (StringUtils.isBlank(endTime)
                    && StringUtils.isNotBlank(startTime));
            if (existOneNotEmpty || !DateUtil
                .isValidDateTime(startTime + PersonalMonitor.ZERO_HOUR_SUFFIX,
                    endTime + PersonalMonitor.LAST_HOUR_SUFFIX, DateFormatInstant.simpleDateTimeFormatter)) {
                JSONObject json = new JSONObject();
                json.put("count", 0);
                json.put("records", new ArrayList<>());
                json.put("summary", new PersonMonitorSummaryVO());
                result = CommonResult.success(json, "监控时间不合法");
            } else {
                result = iPersonalMonitorLogService
                    .listPersonMonitorInfo(name, startTime + PersonalMonitor.ZERO_HOUR_SUFFIX,
                        endTime + PersonalMonitor.LAST_HOUR_SUFFIX, action, groupId, page, size);
            }
        }
        return result;
    }

    @ApiOperation(value = "下载员工考勤报表", notes = "下载员工考勤报表")
    @PreAuthorize("hasAuthority('attendance')")
    @GetMapping(value = "/exportAttendance/{date}")
    public void exportAttendanceExcel(@NotBlank(message = "日期不能为空") @PathVariable String date, HttpServletResponse response) {
        long t = System.currentTimeMillis();
        log.info("开始下载员工考勤报表..");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            date += "-01";
            Date selectedDate = DateFormatInstant.simpleDateFormatter.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            // 设置响应输出的头类型及下载文件的默认名称
            String tableName = getExportExcelFileName(cal);
            //String fileName = new String(tableName.getBytes("utf-8"), "ISO-8859-1");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(tableName, "UTF-8"));
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            XSSFWorkbook workbook = iPersonalMonitorLogService.exportAttendanceExcel(cal);
            if (workbook == null) {
                log.error("workbook为空..");
                return;
            }
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            log.error("export异常：{}", e.getMessage());
        }
        log.info("下载员工考勤报表完成,花费时间: " + (System.currentTimeMillis() - t));
    }

    //获取导出excel文件名称
    private String getExportExcelFileName(Calendar cal) {
        String fileName = (cal.get(Calendar.MONTH) + 1) + "月份考勤数据报表.xlsx";
        return fileName;
    }

    @ApiOperation(value = "下载员工打卡列表", notes = "下载员工打卡excel列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "date", value = "日期", dataType = "String", paramType = "query")
    })
    @GetMapping(value = "/export/{date}")
    public void export(@NotBlank(message = "日期不能为空") @PathVariable String date, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        String startTime = date + PersonalMonitor.ZERO_HOUR_SUFFIX;
        String endTime = date + PersonalMonitor.LAST_HOUR_SUFFIX;
        try {
            LambdaQueryWrapper<PersonalInfo> personalInfoQueryWrapper = new LambdaQueryWrapper<>();
            personalInfoQueryWrapper.eq(PersonalInfo::getDeleted, 0);
            Map<Long, String> personalMap = iPersonalInfoService.list(personalInfoQueryWrapper).stream()
                .collect(Collectors.toMap(key -> key.getId(), val -> val.getName()));
            LambdaQueryWrapper<PersonalMonitorLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(PersonalMonitorLog::getCreateTime, startTime)
                .le(PersonalMonitorLog::getCreateTime, endTime);
            queryWrapper.eq(PersonalMonitorLog::getSource, 0);
            queryWrapper.orderByAsc(PersonalMonitorLog::getCreateTime);
            List<PersonalMonitorLog> personalMonitorLogList = iPersonalMonitorLogService.list(queryWrapper);
            Map<Long, List<PersonalMonitorLog>> monitorLogMap = new HashMap<>();
            personalMonitorLogList.stream().forEach(personalMonitorLog -> {
                Long personalId = personalMonitorLog.getPersonalId();
                if (monitorLogMap.get(personalId) == null) {
                    List<PersonalMonitorLog> logs = new ArrayList<>();
                    logs.add(personalMonitorLog);
                    monitorLogMap.put(personalId, logs);
                } else {
                    monitorLogMap.get(personalId).add(personalMonitorLog);
                }
            });
            List<PersonalMonitorDetailVO> vos = new ArrayList<>();
            PersonalMonitorDetailVO vo;
            for (Map.Entry<Long, List<PersonalMonitorLog>> entry : monitorLogMap.entrySet()) {
                Long key = entry.getKey();
                List<PersonalMonitorLog> value = entry.getValue();
                if (value == null || value.isEmpty()) {
                    continue;
                }
                vo = new PersonalMonitorDetailVO();
                vo.setPersonName(personalMap.get(key));
                int size = value.size();
                vo.setInTime(DateFormatInstant.timeFormatter.format(value.get(0).getCreateTime()));
                if (size == 1) {
                    vo.setOutTime("");
                } else {
                    vo.setOutTime(DateFormatInstant.timeFormatter.format(value.get(size - 1).getCreateTime()));
                }
                vo.setDate(date);
                vos.add(vo);
                personalMap.remove(key);
            }
            List<PersonalMonitorDetailVO> emptyVos = personalMap.values().stream().map(val ->
                new PersonalMonitorDetailVO(date, val, "", "")).collect(Collectors.toList());
            vos.addAll(emptyVos);
            // 设置响应输出的头类型及下载文件的默认名称
            String tableName = "员工打卡记录-" + date + ".xls";
            String fileName = new String(tableName.getBytes("utf-8"), "ISO-8859-1");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            //导出
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), PersonalMonitorDetailVO.class, vos);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            log.info("导出excel消耗时间(ms): " + (System.currentTimeMillis() - start));
        } catch (IOException e) {
            log.error("export异常：{}", e.getMessage());
        }
    }
}
