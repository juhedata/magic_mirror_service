package com.juhe.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.juhe.demo.api.ArcFaceAnalysis;
import com.juhe.demo.bo.ExportDateInfo;
import com.juhe.demo.bo.PersonAttendance;
import com.juhe.demo.bo.PersonalBO;
import com.juhe.demo.bo.StatisticPersonAttendance;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.config.ArcFacePool;
import com.juhe.demo.constant.SystemConstant;
import com.juhe.demo.constant.SystemConstant.Common;
import com.juhe.demo.constant.SystemConstant.DateFormatInstant;
import com.juhe.demo.constant.SystemConstant.Personal;
import com.juhe.demo.constant.SystemConstant.PersonalMonitor;
import com.juhe.demo.constant.SystemConstant.RedisKeyPrefix;
import com.juhe.demo.entity.GroupName;
import com.juhe.demo.entity.PersonalIcon;
import com.juhe.demo.entity.PersonalInfo;
import com.juhe.demo.entity.PersonalMac;
import com.juhe.demo.entity.PersonalMonitorLog;
import com.juhe.demo.mapper.PersonalMonitorLogMapper;
import com.juhe.demo.service.IGroupNameService;
import com.juhe.demo.service.IPersonalIconService;
import com.juhe.demo.service.IPersonalInfoService;
import com.juhe.demo.service.IPersonalMacService;
import com.juhe.demo.service.IPersonalMonitorLogService;
import com.juhe.demo.service.IPersonalMonitorStatictisService;
import com.juhe.demo.transfer.PersonMonitorConditionBO;
import com.juhe.demo.transfer.SimilarCompareBo;
import com.juhe.demo.util.RedisUtils;
import com.juhe.demo.vo.PersonMonitorInfoVO;
import com.juhe.demo.vo.PersonMonitorSummaryVO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor.RED;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 人员监控信息 服务实现类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Scope("prototype")
@Service
@Slf4j
public class PersonalMonitorLogServiceImpl extends ServiceImpl<PersonalMonitorLogMapper, PersonalMonitorLog> implements IPersonalMonitorLogService {

    @Value("${personal.monitor.expire}")
    private Long keyExpireTime;

    @Value("${baidu.face.liveness}")
    private float faceLivenessValue;

    @Value("${cache.open}")
    private Integer cacheOpen;

    @Value("${excel.export.template.path}")
    private String excelExportTemplatePath;

    @Autowired
    private IPersonalInfoService iPersonalInfoService;

    @Autowired
    private IPersonalMacService iPersonalMacService;

    @Autowired
    private IPersonalIconService iPersonalIconService;

    @Autowired
    private IPersonalMonitorStatictisService iPersonalMonitorStatictisService;

    @Autowired
    private IGroupNameService iGroupNameService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ArcFacePool arcFacePool;

    @Override
    public void monitorMac(String macStr) {
        LocalDate localDate = LocalDate.now();
        List<String> macs = Arrays.asList(macStr.split(Common.DELIMITER));
        String key;
        for (String mac : macs) {
            key = RedisKeyPrefix.MONITORPROBE + mac;
            //判断5秒之内是否已经分析过
            boolean isCache = (cacheOpen == 1 && !redisUtils.existKey(key));
            if (cacheOpen == 0 || isCache) {
                if (isCache) {
                    redisUtils.set(key, String.valueOf(System.currentTimeMillis()), keyExpireTime);
                }
                LambdaQueryWrapper<PersonalMac> macLambdaQueryWrapper = new LambdaQueryWrapper<>();
                macLambdaQueryWrapper.eq(PersonalMac::getMac, mac);
                List<PersonalMac> personalMacs = iPersonalMacService.list(macLambdaQueryWrapper);
                if (personalMacs != null && !personalMacs.isEmpty()) {
                    for (PersonalMac personalMac : personalMacs) {
                        //查询是否打卡
                        LambdaQueryWrapper<PersonalMonitorLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        logLambdaQueryWrapper.eq(PersonalMonitorLog::getAction, 0)
                            .eq(PersonalMonitorLog::getPersonalId, personalMac.getPersonalId())
                            .gt(PersonalMonitorLog::getCreateTime, localDate)
                            .lt(PersonalMonitorLog::getCreateTime, localDate.plusDays(1));
                        int count = count(logLambdaQueryWrapper);
                        if (count == 0) {
                            PersonalMonitorLog personalMonitorLog = new PersonalMonitorLog(personalMac.getPersonalId(),
                                0, 1, LocalDateTime.now());
                            iPersonalMonitorStatictisService.statisticPersonMonitorData(personalMac.getPersonalId(), 0);
                            save(personalMonitorLog);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<PersonalBO> monitorImage(String image, Long action, String imageName) {
        LocalDate localDate = LocalDate.now();
        List<PersonalBO> personalBOList = new ArrayList<>();
        int iconCount = iPersonalIconService.count();
        if (iconCount == 0) {
            log.error("库中不存在特征值..");
            return personalBOList;
        }
        byte[] curFeature;
        List<SimilarCompareBo> compareBos = null;
        ArcFaceAnalysis arcFaceAnalysis = null;
        try {
            arcFaceAnalysis = arcFacePool.borrowObject();
            curFeature = arcFaceAnalysis.getEncodeStrFaceFeature(image);
            if (curFeature == null || curFeature.length == 0) {
                log.error("当前人员特征值为空..");
                return personalBOList;
            }
            int countPage = iconCount / Personal.FETCH_SIZE;
            IPage<PersonalIcon> iPage;
            List<PersonalIcon> records;
            compareBos = new ArrayList<>();
            for (int i = 0; i <= countPage; i++) {
                iPage = iPersonalIconService
                    .page(new Page<>(countPage + 1, Personal.FETCH_SIZE), new LambdaQueryWrapper<>());
                records = iPage.getRecords();
                ArcFaceAnalysis finalArcFaceAnalysis = arcFaceAnalysis;
                compareBos.addAll(
                    records.parallelStream()
                        .map(val -> finalArcFaceAnalysis.similarCompare(curFeature,
                            val.getFeature(), val.getPersonalId())).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            arcFacePool.returnObject(arcFaceAnalysis);
        }
        Optional<SimilarCompareBo> bo = compareBos.stream().filter(val -> val != null)
            .sorted((o1, o2) -> o2.getScore().compareTo(o1.getScore())).findFirst();
        if (!bo.isPresent()) {
            log.error("人员相似度小于设定值..");
            return personalBOList;
        }
        log.info("传入的图片对比最大相似度为: " + bo.get().getScore());
        Long userId = bo.get().getId();
        PersonalInfo personalInfo = iPersonalInfoService.getById(userId);
        if (personalInfo != null) {
            //活体检测
            /*JSONObject verifyRst = aipBodyAnalysis.faceVerify(image);
            String errorCode = String.valueOf(verifyRst.get("error_code"));
            if (!AipError.SUCCESS.getErrorCode().equals(errorCode)) {
                log.error("活体检测失败: " + JSON.toJSONString(verifyRst));
                return personalBOList;
            }
            Double faceLiveness = verifyRst.getJSONObject("result").getDouble("face_liveness");
            log.info("图片活体识别值为: " + faceLiveness);
            if (faceLiveness < faceLivenessValue) {
                return personalBOList;
            }*/
            log.debug("识别到的人员名称:" + personalInfo.getName() + " ,图片名称:" + imageName);
            PersonalBO personalBO = new PersonalBO();
            BeanUtils.copyProperties(personalInfo, personalBO);
            personalBOList.add(personalBO);
            String keyPrefix =
                (action == 0) ? SystemConstant.RedisKeyPrefix.MONITORIN : RedisKeyPrefix.MONITOROUT;
            boolean isCache = (cacheOpen == 1 && !redisUtils.existKey(keyPrefix + userId));
            //判断5秒之内是否已经分析过[如已分析不作操作]
            if (cacheOpen == 0 || isCache) {
                if (isCache) {
                    redisUtils
                        .set(keyPrefix + userId, String.valueOf(System.currentTimeMillis()), keyExpireTime);
                }
                //statistics Personal and log
                LambdaQueryWrapper<PersonalMonitorLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
                logLambdaQueryWrapper.eq(PersonalMonitorLog::getAction, 0)
                    .eq(PersonalMonitorLog::getSource, 1)
                    .eq(PersonalMonitorLog::getPersonalId, userId)
                    .gt(PersonalMonitorLog::getCreateTime, localDate)
                    .lt(PersonalMonitorLog::getCreateTime, localDate.plusDays(1));
                int count = count(logLambdaQueryWrapper);
                if (!(action == 0 && count == 1)) {
                    PersonalMonitorLog personalMonitorLog = new PersonalMonitorLog(userId, action.intValue(), 0,
                        LocalDateTime.now());
                    iPersonalMonitorStatictisService.statisticPersonMonitorData(userId, action.intValue());
                    save(personalMonitorLog);
                }
            }
        } else {
            log.error("人员不存在..");
        }
        return personalBOList;
    }

    @Override
    public CommonResult listPersonMonitorInfo(String name, String startTime, String endTime, Integer action,
        Long groupId, Integer page, Integer size) {
        Pagination pagination = new Pagination(page, size);
        PersonMonitorConditionBO condition = new PersonMonitorConditionBO(name, startTime, endTime, action, groupId);
        Long count = baseMapper.countPersonMonitorInfo(condition);
        List<PersonMonitorInfoVO> personMonitorInfos = baseMapper.listPersonMonitorInfo(pagination, condition);
        PersonMonitorSummaryVO summaryBO = iPersonalMonitorStatictisService.summaryPersonMonitorInfo(condition);
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        json.put("count", count);
        json.put("records", personMonitorInfos);
        json.put("summary", (summaryBO == null) ? new PersonMonitorSummaryVO() : summaryBO);
        return CommonResult.success(json);
    }

    @Override
    public XSSFWorkbook exportAttendanceExcel(Calendar cal) {
        ExportDateInfo dateInfo = getSelectedMonthDateInfo(cal);
        //获取模板文件
        File template = new File(excelExportTemplatePath);
        XSSFWorkbook workbook = null;
        try (FileInputStream fis = new FileInputStream(template)) {
            final XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fis);
            workbook = xssfWorkbook;
            XSSFSheet xssfSheet = workbook.getSheetAt(0);
            workbook.setSheetName(0, "考勤记录表");
            //微软雅黑字体
            XSSFFont microFont = workbook.createFont();
            microFont.setFontName(PersonalMonitor.FONT_NAME);
            XSSFRow statisticDateRow = xssfSheet.getRow(1);
            XSSFCell statisticDateCell = statisticDateRow.getCell(0);
            statisticDateCell.setCellValue("统计日期: " + dateInfo.getBeginDate() + " ~ " + dateInfo.getEndDate());
            int maxDay = dateInfo.getMaxDay();
            int maxColumnNum = maxDay + 4;
            //删除多余的列
            int deleteColumnNum = PersonalMonitor.MAX_DAYS - maxDay;
            if (deleteColumnNum > 0) {
                int deleteMinColumnNum = 34 - deleteColumnNum;
                int deleteMaxColumnNum = 34;
                for (int i = deleteMaxColumnNum; i > deleteMinColumnNum; i--) {
                    deleteColumn(xssfSheet, i);
                }
            }
            //标红
            XSSFCellStyle redStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontName(PersonalMonitor.FONT_NAME);
            font.setColor(RED.index);
            redStyle.setFont(font);
            //加粗
            XSSFCellStyle boldStyle = workbook.createCellStyle();
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontName(PersonalMonitor.FONT_NAME);
            boldStyle.setFont(boldFont);
            //设置单元格标红、边框、居中
            XSSFCellStyle redBorderStyle = workbook.createCellStyle();
            redBorderStyle.setFont(font);
            redBorderStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
            redBorderStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            redBorderStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            redBorderStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            redBorderStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //居中
            XSSFCellStyle rangeStyle = workbook.createCellStyle();
            rangeStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            rangeStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            rangeStyle.setFont(microFont);
            //设置第四行的周几数据(第4列开始)+dateInfo.getMaxDay()列
            XSSFRow weekDayRow = xssfSheet.getRow(3);
            Map<Integer, String> dayWeekMap = dateInfo.getDayWeekMap();
            //记录周六、周日的列号
            List<Integer> weekendColumns = new ArrayList<>();
            XSSFCell weekDayCell;
            for (int i = 4; i < maxColumnNum; i++) {
                weekDayCell = weekDayRow.getCell(i);
                String weekStr = dayWeekMap.get(i - 3);
                weekDayCell.setCellValue(weekStr);
                if ("六".equals(weekStr) || "日".equals(weekStr)) {
                    weekendColumns.add(i);
                }
            }
            //查询所有分组
            List<GroupName> groups = iGroupNameService.list(new LambdaQueryWrapper<GroupName>().eq(GroupName::getDeleteable, 0));
            Map<Long, String> groupMap = groups.stream().collect(Collectors.toMap(key -> key.getId(), val -> val.getName()));
            //查询所有人员
            LambdaQueryWrapper<PersonalInfo> personalInfoWrapper = new LambdaQueryWrapper<>();
            personalInfoWrapper.eq(PersonalInfo::getDeleted, 0);
            List<PersonalInfo> personalInfos = iPersonalInfoService.list(personalInfoWrapper);
            //员工考勤信息
            Map<Long, StatisticPersonAttendance> statisticPersonAttendanceMap = getPersonAttendances(cal);
            PersonalInfo personalInfo;
            int personSize = personalInfos.size();
            int firstCellNo = 4;
            for (int j = 0; j < personSize; j++) {
                personalInfo = personalInfos.get(j);
                int firstRowNo = 4 * (j + 1);
                int lastRowNo = 4 * (j + 1) + 3;
                XSSFRow firstRow = xssfSheet.createRow(firstRowNo);
                XSSFRow latestRow = xssfSheet.createRow(firstRowNo + 1);
                XSSFRow overtimeRow = xssfSheet.createRow(firstRowNo + 2);
                XSSFRow attendanceRow = xssfSheet.createRow(firstRowNo + 3);
                //员工号
                CellRangeAddress employNoRangeAddress = new CellRangeAddress(firstRowNo, lastRowNo, 0, 0);
                xssfSheet.addMergedRegion(employNoRangeAddress);
                XSSFCell xssfCell_0 = firstRow.createCell(0);
                xssfCell_0.setCellValue(personalInfo.getEmployeeNo());
                xssfCell_0.setCellStyle(rangeStyle);
                //姓名
                CellRangeAddress nameRangeAddress = new CellRangeAddress(firstRowNo, lastRowNo, 1, 1);
                xssfSheet.addMergedRegion(nameRangeAddress);
                XSSFCell xssfCell_1 = firstRow.createCell(1);
                xssfCell_1.setCellValue(personalInfo.getName());
                xssfCell_1.setCellStyle(rangeStyle);
                //所属部门
                CellRangeAddress departmentRangeAddress = new CellRangeAddress(firstRowNo, lastRowNo, 2, 2);
                xssfSheet.addMergedRegion(departmentRangeAddress);
                XSSFCell xssfCell_2 = firstRow.createCell(2);
                xssfCell_2.setCellValue(groupMap.get(personalInfo.getGroupId()));
                xssfCell_2.setCellStyle(rangeStyle);
                //每日打卡时间
                //最早
                firstRow.createCell(3).setCellValue(PersonalMonitor.EARLIEST_STR);
                //最晚
                latestRow.createCell(3).setCellValue(PersonalMonitor.LATEST_STR);
                //加班时长
                overtimeRow.createCell(3).setCellValue(PersonalMonitor.OVERTIME_STR);
                //出勤时间
                attendanceRow.createCell(3).setCellValue(PersonalMonitor.ATTENDANCE_STR);
                //出勤时间<9小时、或周末全部标红
                StatisticPersonAttendance statisticPersonAttendance = statisticPersonAttendanceMap.get(personalInfo.getId());
                if (statisticPersonAttendance == null) {
                    statisticPersonAttendance = initStatisticPersonAttendance(personalInfo.getId(), cal);
                }
                PersonAttendance personAttendance;
                for (int i = 0; i < maxDay; i++) {
                    int cellIndex = firstCellNo + i;
                    personAttendance = statisticPersonAttendance.getAttendances().get(i);
                    firstRow.createCell(cellIndex).setCellValue(personAttendance.getEarliestTime());
                    latestRow.createCell(cellIndex).setCellValue(personAttendance.getLatestTime());
                    XSSFCell overtimeCell = overtimeRow.createCell(cellIndex);
                    overtimeCell.setCellValue(personAttendance.getOverTime());
                    overtimeCell.setCellStyle(redStyle);
                    XSSFCell attendanceCell = attendanceRow.createCell(cellIndex);
                    attendanceCell.setCellValue(personAttendance.getAttendanceTime());
                    if (personAttendance.isRed()) {
                        attendanceCell.setCellStyle(redStyle);
                    }
                }
                //工作日总加班时间
                XSSFCell maxOverTimeDayCell = overtimeRow.createCell(maxColumnNum);
                maxOverTimeDayCell.setCellStyle(boldStyle);
                maxOverTimeDayCell.setCellValue(statisticPersonAttendance.getWeekDayTotalOverTime());
                //工作日总出勤时间
                XSSFCell maxAttendanceCell = attendanceRow.createCell(maxColumnNum);
                maxAttendanceCell.setCellStyle(boldStyle);
                maxAttendanceCell.setCellValue(statisticPersonAttendance.getWeekDayTotalAttendanceTime());
            }
            //修改单元格样式(设置周六、周日列为标红样式、部分单元格加边框，统一设置宽度)
            int lastRowNum = xssfSheet.getLastRowNum();
            for (int i = 0; i <= lastRowNum; i++) {
                //忽略统计日期行与标题行
                if (i != 1 && i != 0) {
                    XSSFRow r = xssfSheet.getRow(i);
                    int cellNum = r.getLastCellNum();
                    for (int j = 0; j < cellNum; j++) {
                        XSSFCell cell = r.getCell(j);
                        if (cell == null) {
                            cell = r.createCell(cellNum);
                        }
                        if (weekendColumns.contains(j)) {
                            if (i == 2 || i == 3) {
                                cell.setCellStyle(redBorderStyle);
                            } else {
                                //边框标红
                                cell.setCellStyle(redStyle);
                            }
                        }
                    }
                }
            }
            //微软雅黑字体
            workbook.getFontAt((short) 0).setFontName(PersonalMonitor.FONT_NAME);
            for (int j = 0; j <= maxColumnNum; j++) {
                xssfSheet.setColumnWidth(j, 3060);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("export异常：{}", e.getMessage());
        }
        return workbook;
    }

    private StatisticPersonAttendance initStatisticPersonAttendance(long personId, Calendar cal) {
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<PersonAttendance> attendances = new ArrayList<>(maxDays);
        for (int i = 1; i <= maxDays; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            PersonAttendance attendance = new PersonAttendance(DateFormatInstant.simpleDateFormatter.format(cal.getTime()),
                "", "", "", "");
            attendances.add(attendance);
        }
        return new StatisticPersonAttendance(personId, attendances, "", "");
    }

    private Map<Long, StatisticPersonAttendance> getPersonAttendances(Calendar cal) {
        String beginTime;
        String endTime;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        beginTime = DateFormatInstant.simpleDateFormatter.format(cal.getTime()) + PersonalMonitor.ZERO_HOUR_SUFFIX;
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, maxDays);
        endTime = DateFormatInstant.simpleDateFormatter.format(cal.getTime()) + PersonalMonitor.LAST_HOUR_SUFFIX;
        LambdaQueryWrapper<PersonalMonitorLog> monitorLogWrapper = new LambdaQueryWrapper<>();
        monitorLogWrapper.ge(PersonalMonitorLog::getCreateTime, beginTime)
            .le(PersonalMonitorLog::getCreateTime, endTime).eq(PersonalMonitorLog::getSource, 0);
        monitorLogWrapper.orderByAsc(PersonalMonitorLog::getCreateTime);
        List<PersonalMonitorLog> personalMonitorLogs = list(monitorLogWrapper);
        Map<Long, List<PersonalMonitorLog>> monitorLogGroup = personalMonitorLogs.stream().sorted(Comparator.comparing(PersonalMonitorLog::getCreateTime)).
            collect(Collectors.groupingBy(PersonalMonitorLog::getPersonalId));
        Map<Long, StatisticPersonAttendance> statisticPersonAttendanceMap = new HashMap<>();
        for (Map.Entry<Long, List<PersonalMonitorLog>> entry : monitorLogGroup.entrySet()) {
            StatisticPersonAttendance statisticPersonAttendance = new StatisticPersonAttendance();
            //日期-日志集合记录
            Map<String, List<PersonalMonitorLog>> monitorDateLogMap = new HashMap<>();
            for (PersonalMonitorLog monitorLog : entry.getValue()) {
                LocalDateTime createTime = monitorLog.getCreateTime();
                String createTimeStr = DateFormatInstant.DATE_FORMATTER.format(createTime);
                if (monitorDateLogMap.containsKey(createTimeStr)) {
                    monitorDateLogMap.get(createTimeStr).add(monitorLog);
                } else {
                    List<PersonalMonitorLog> monitorLogs = new ArrayList<>();
                    monitorLogs.add(monitorLog);
                    monitorDateLogMap.put(createTimeStr, monitorLogs);
                }
            }
            List<PersonAttendance> attendances = new ArrayList<>();
            //遍历月天数
            PersonAttendance attendance;
            //出勤时长
            Integer totalAttendanceMinutes = 0;
            //加班时长
            Integer totalOverTimeMinutes = 0;
            for (int i = 1; i <= maxDays; i++) {
                cal.set(Calendar.DAY_OF_MONTH, i);
                String date = DateFormatInstant.simpleDateFormatter.format(cal.getTime());
                if (monitorDateLogMap.containsKey(date)) {
                    List<PersonalMonitorLog> dateLogs = monitorDateLogMap.get(date);
                    if (dateLogs == null || dateLogs.isEmpty()) {
                        attendance = new PersonAttendance(date, "", "", "", "");
                    } else if (dateLogs.size() == 1) {
                        attendance = new PersonAttendance(date, DateFormatInstant.timeFormatter.format(dateLogs.get(0).getCreateTime()),
                            "", "", "");
                    } else {
                        LocalDateTime inTime = dateLogs.get(0).getCreateTime();
                        LocalDateTime outTime = dateLogs.get(dateLogs.size() - 1).getCreateTime();
                        attendance = new PersonAttendance();
                        attendance.setDate(date);
                        attendance.setEarliestTime(DateFormatInstant.timeFormatter.format(inTime));
                        attendance.setLatestTime(DateFormatInstant.timeFormatter.format(outTime));
                        //工作日(算出勤与加班)、周末(算加班)
                        String[] overTime;
                        if (!isDateWeekend(inTime)) {
                            String[] attendanceTime = calAttendanceTime(inTime, outTime);
                            attendance.setAttendanceTime(attendanceTime[0]);
                            attendance.setRed(changeAttendanceCellStyle(attendanceTime[0]));
                            totalAttendanceMinutes += Integer.parseInt(attendanceTime[1]);
                            //晚7点之后计算
                            overTime = calOverTime(inTime, outTime, 0);
                        } else {
                            //周末
                            overTime = calOverTime(inTime, outTime, 1);
                        }
                        attendance.setOverTime(overTime[0]);
                        totalOverTimeMinutes += Integer.parseInt(overTime[1]);
                    }
                } else {
                    attendance = new PersonAttendance(date, "", "", "", "");
                }
                attendances.add(attendance);
            }
            statisticPersonAttendance.setPersonId(entry.getKey());
            statisticPersonAttendance.setAttendances(attendances);
            //工作日总加班时长
            statisticPersonAttendance.setWeekDayTotalOverTime(getTotalCalPeriod(totalOverTimeMinutes));
            //工作日总出勤时长
            statisticPersonAttendance.setWeekDayTotalAttendanceTime(getTotalCalPeriod(totalAttendanceMinutes));
            statisticPersonAttendanceMap.put(entry.getKey(), statisticPersonAttendance);
        }
        return statisticPersonAttendanceMap;
    }

    //是否出勤时间单元格标红
    private static boolean changeAttendanceCellStyle(String attendanceTime) {
        int index = attendanceTime.indexOf(PersonalMonitor.HOUR_STR);
        if (index != -1) {
            int hour = Integer.parseInt(attendanceTime.substring(0, index));
            if (hour < 9) {
                return true;
            }
        }
        return false;
    }

    private String getTotalCalPeriod(int minutes) {
        String rst = "";
        if (minutes > 0) {
            int hour = minutes / 60;
            int minute = minutes % 60;
            if (hour == 0) {
                rst = minute + PersonalMonitor.MINUTE_STR;
            } else {
                rst = hour + PersonalMonitor.HOUR_STR + minute + PersonalMonitor.MINUTE_STR;
            }
        }
        return rst;
    }

    //计算加班时间
    private String[] calOverTime(LocalDateTime earliestTime, LocalDateTime latestTime, int type) {
        String[] rst = new String[2];
        rst[0] = "";
        rst[1] = "0";
        LocalDateTime beginCalTime;
        Duration duration;
        //工作日
        if (type == 0) {
            //工作日九点半之后到不算加班
            LocalDateTime allowCalOverTimeOut = earliestTime.withHour(9).withMinute(30).withSecond(59);
            if (earliestTime.isAfter(allowCalOverTimeOut)) {
                return rst;
            }
            beginCalTime = earliestTime.withHour(19).withMinute(0).withSecond(0);
        } else {
            beginCalTime = earliestTime;
        }
        if (latestTime.isAfter(beginCalTime)) {
            beginCalTime = beginCalTime.withSecond(0);
            latestTime = latestTime.withSecond(0);
            duration = Duration.between(beginCalTime, latestTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            if (hours == 0) {
                rst[0] = minutes + PersonalMonitor.MINUTE_STR;
            } else {
                rst[0] = hours + PersonalMonitor.HOUR_STR + minutes + PersonalMonitor.MINUTE_STR;
            }
            rst[1] = String.valueOf(duration.toMinutes());
        }
        return rst;
    }

    //计算出勤时间
    private String[] calAttendanceTime(LocalDateTime earliestTime, LocalDateTime latestTime) {
        String[] rst = new String[2];
        earliestTime = earliestTime.withSecond(0);
        latestTime = latestTime.withSecond(0);
        Duration duration = Duration.between(earliestTime, latestTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        if (hours == 0) {
            rst[0] = minutes + PersonalMonitor.MINUTE_STR;
        } else {
            rst[0] = hours + PersonalMonitor.HOUR_STR + minutes + PersonalMonitor.MINUTE_STR;
        }
        rst[1] = String.valueOf(duration.toMinutes());
        return rst;
    }

    private boolean isDateWeekend(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }

    private ExportDateInfo getSelectedMonthDateInfo(Calendar cal) {
        ExportDateInfo exportDateInfo = new ExportDateInfo();
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        exportDateInfo.setMaxDay(maxDay);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        exportDateInfo.setBeginDate(PersonalMonitor.DATE_FORMAT.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_MONTH, maxDay);
        exportDateInfo.setEndDate(PersonalMonitor.DATE_FORMAT.format(cal.getTime()));
        Map<Integer, String> dayWeekMap = new LinkedHashMap<>();
        for (int i = 1; i <= maxDay; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            String weekDay = PersonalMonitor.WEEK_DAYS[cal.get(Calendar.DAY_OF_WEEK) - 1];
            dayWeekMap.put(i, weekDay);
        }
        exportDateInfo.setDayWeekMap(dayWeekMap);
        return exportDateInfo;
    }

    /**
     * 删除列
     */
    private void deleteColumn(Sheet sheet, int column) {
        for (int rId = 0; rId <= sheet.getLastRowNum(); rId++) {
            Row row = sheet.getRow(rId);
            for (int cID = column; cID <= row.getLastCellNum(); cID++) {
                Cell cOld = row.getCell(cID);
                if (cOld != null) {
                    row.removeCell(cOld);
                }
                Cell cNext = row.getCell(cID + 1);
                if (cNext != null) {
                    Cell cNew = row.createCell(cID, cNext.getCellTypeEnum());
                    cloneCell(cNew, cNext);
                    //Set the column width only on the first row.
                    //Other wise the second row will overwrite the original column width set previously.
                    if (rId == 0) {
                        sheet.setColumnWidth(cID, sheet.getColumnWidth(cID + 1));
                    }
                }
            }
        }
    }

    /**
     * 右边列左移
     */
    private void cloneCell(Cell cNew, Cell cOld) {
        cNew.setCellComment(cOld.getCellComment());
        cNew.setCellStyle(cOld.getCellStyle());
        if (CellType.BOOLEAN == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getBooleanCellValue());
        } else if (CellType.NUMERIC == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getNumericCellValue());
        } else if (CellType.STRING == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getStringCellValue());
        } else if (CellType.ERROR == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getErrorCellValue());
        } else if (CellType.FORMULA == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getCellFormula());
        }
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Author xuman.xu
     * @Description 获取照片表情
     * @Date 15:39 2019/7/15
     * @Param [image]
     **/
    /*private Map<String, String> getEmotionStringMap(String image) {
        Map<String, String> faceEmotion = new HashMap<>(8);
        //调用人脸识别，获取表情
        HashMap<String, String> options = new HashMap<>(8);
        options.put("face_field", "age,beauty,expression,gender,glasses,race,eye_status,emotion,face_type");
        options.put("max_face_num", "8");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "NONE");
        JSONObject jsonDetect = aipBodyAnalysis.detect(image, "BASE64", options);
        String errorMsg = jsonDetect.optString("error_msg", "FAILED");
        if (!SystemConstant.BackResult.SUCCESS.equals(errorMsg)) {
            return faceEmotion;
        }
        JSONObject jsonResult = jsonDetect.getJSONObject("result");
        int faceNum = jsonResult.optInt("face_num", 0);
        if (faceNum == 0) {
            return faceEmotion;
        }
        JSONArray faceListJson = jsonResult.getJSONArray("face_list");
        JSONObject faceInfo;
        for (int i = 0; i < faceNum; i++) {
            faceInfo = faceListJson.getJSONObject(i);
            String faceToken = faceInfo.getString("face_token");
            String emotion = faceInfo.getJSONObject("emotion").getString("type");
            faceEmotion.put(faceToken, emotion);
        }
        return faceEmotion;
    }*/
}
