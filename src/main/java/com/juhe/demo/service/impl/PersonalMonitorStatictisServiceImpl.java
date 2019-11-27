package com.juhe.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juhe.demo.constant.SystemConstant;
import com.juhe.demo.constant.SystemConstant.PersonalMonitor;
import com.juhe.demo.entity.PersonalMonitorLog;
import com.juhe.demo.entity.PersonalMonitorStatictis;
import com.juhe.demo.mapper.PersonalMonitorLogMapper;
import com.juhe.demo.mapper.PersonalMonitorStatictisMapper;
import com.juhe.demo.service.IPersonalMonitorStatictisService;
import com.juhe.demo.transfer.CommonBO;
import com.juhe.demo.transfer.PersonMonitorConditionBO;
import com.juhe.demo.vo.DiscreteFlowMonitorVO;
import com.juhe.demo.vo.DiscreteFlowNumVO;
import com.juhe.demo.vo.PersonMonitorSummaryVO;
import com.juhe.demo.vo.PersonalMonitorScreenVO;
import com.juhe.demo.vo.PersonalStatictisAnalysisVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 人员监控统计信息 服务实现类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-15
 */
@Service
public class PersonalMonitorStatictisServiceImpl extends
    ServiceImpl<PersonalMonitorStatictisMapper, PersonalMonitorStatictis> implements IPersonalMonitorStatictisService {

    @Resource
    private PersonalMonitorLogMapper personalMonitorLogMapper;

    /**
     * @return void
     * @Author xxmfypp
     * @Description 统计监控数据
     * @Date 11:50 2019/7/16
     * @Param [userId, action] 用户id,操作[0:入,1:出]
     **/
    @Override
    public synchronized void statisticPersonMonitorData(Long userId, Integer action) {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        LambdaQueryWrapper<PersonalMonitorStatictis> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(PersonalMonitorStatictis::getStatisticsTime, localDate)
            .eq(PersonalMonitorStatictis::getPersonalId, userId);
        List<PersonalMonitorStatictis> personalMonitorStatisticList = list(queryWrapper);
        PersonalMonitorStatictis personalMonitorStatictis = null;
        if (personalMonitorStatisticList != null && !personalMonitorStatisticList.isEmpty()) {
            personalMonitorStatictis = personalMonitorStatisticList.get(0);
        }
        int addIn = (action == 0) ? 1 : 0;
        int addOut = (action == 1) ? 1 : 0;
        if (personalMonitorStatictis == null) {
            personalMonitorStatictis = new PersonalMonitorStatictis(userId, addOut, addIn, 0L,
                (action == 0) ? localTime : null, null, localDate);
        } else {
            personalMonitorStatictis.setInCount(personalMonitorStatictis.getInCount() + addIn);
            personalMonitorStatictis.setOutCount(personalMonitorStatictis.getOutCount() + addOut);
            //出,记录与上一次进的时间差累加standTime
            if (action == 1) {
                if (personalMonitorStatictis.getEarliestTime() != null) {
                    personalMonitorStatictis.setLatestTime(localTime);
                    //最近一次记录为进
                    LambdaQueryWrapper<PersonalMonitorLog> lastQueryWrapper = new LambdaQueryWrapper();
                    lastQueryWrapper.eq(PersonalMonitorLog::getPersonalId, userId)
                        .orderByDesc(PersonalMonitorLog::getCreateTime);
                    IPage<PersonalMonitorLog> iPage = personalMonitorLogMapper
                        .selectPage(new Page<>(1, 1), lastQueryWrapper);
                    List<PersonalMonitorLog> logs = iPage.getRecords();
                    if (logs != null && !logs.isEmpty() && logs.get(0).getAction() == 0) {
                        personalMonitorStatictis.setStandTime(personalMonitorStatictis.getStandTime() + Long.valueOf(
                            localTime.toSecondOfDay() - personalMonitorStatictis.getEarliestTime().toSecondOfDay()));
                    }
                }
            } else {
                //进,记录earliestTime
                personalMonitorStatictis.setEarliestTime(localTime);
            }
        }
        saveOrUpdate(personalMonitorStatictis);
    }

    @Override
    public PersonMonitorSummaryVO summaryPersonMonitorInfo(PersonMonitorConditionBO condition) {
        return baseMapper.summaryPersonMonitorInfo(condition);
    }

    /**
     * @return com.juhe.demo.vo.PersonalStatictisAnalysisVO
     * @Author kuai.zhang
     * @Description 统计分析
     * @Date 14:09 2019/7/23
     * @Param [dateTypeValid, dateType, startDate, endDate]
     **/
    @Override
    public PersonalStatictisAnalysisVO statisticAnalysis(Boolean dateTypeValid, Integer dateType, String startDate,
        String endDate) {
        String start;
        String end;
        if (dateTypeValid) {
            String[] datePeriod = getDatePeriod(dateType);
            start = datePeriod[0];
            end = datePeriod[1];
        } else {
            start = startDate;
            end = endDate;
        }
        //人流统计
        PersonalStatictisAnalysisVO bo = getPersonQuantityInfo(start, end);
        //实时速率统计
        getPersonInstantRateInfo(bo);
        //人流次数离散图(近7、30天需要按照天显示，今日、昨日按照24小时时间显示)
        List<DiscreteFlowNumVO> discreteFlowNums;
        if (dateTypeValid && (PersonalMonitor.NEARLY_THIRTY_DAYS == dateType
            || PersonalMonitor.NEARLY_SEVEN_DAYS == dateType)) {
            discreteFlowNums = baseMapper.discreteDateFlowNum(start, end);
        } else {
            start += PersonalMonitor.ZERO_HOUR_SUFFIX;
            end += PersonalMonitor.LAST_HOUR_SUFFIX;
            List<DiscreteFlowNumVO> discreteInFlowNums = baseMapper.discreteTimeInFlowNum(start, end);
            List<DiscreteFlowNumVO> discreteOutFlowNums = baseMapper.discreteTimeOutFlowNum(start, end);
            Map<String, Integer> inMap = discreteInFlowNums.stream().filter(val -> val.getInNum() != null)
                .collect(Collectors.toMap(key -> key.getTime(), val -> val.getInNum()));
            Map<String, Integer> outMap = discreteOutFlowNums.stream().filter(val -> val.getOutNum() != null)
                .collect(Collectors.toMap(key -> key.getTime(), val -> val.getOutNum()));
            DiscreteFlowNumVO numBO;
            discreteFlowNums = new ArrayList<>();
            for (Map.Entry<String, Integer> in : inMap.entrySet()) {
                String key = in.getKey();
                numBO = new DiscreteFlowNumVO(key, in.getValue(), 0);
                if (outMap.containsKey(key)) {
                    numBO.setOutNum(outMap.get(key));
                    outMap.remove(key);
                }
                discreteFlowNums.add(numBO);
            }
            for (Map.Entry<String, Integer> out : outMap.entrySet()) {
                numBO = new DiscreteFlowNumVO(out.getKey(), 0, out.getValue());
                discreteFlowNums.add(numBO);
            }
        }
        if (discreteFlowNums != null) {
            discreteFlowNums.sort(Comparator.comparing(DiscreteFlowNumVO::getTime));
            bo.setDiscreteFlowNums(discreteFlowNums);
        }
        return bo;
    }

    @Override
    public PersonalMonitorScreenVO monitorScreenStatistic() {
        String[] datePeriod = getDatePeriod(SystemConstant.PersonalMonitor.TODAY);
        PersonalStatictisAnalysisVO todayAnalysisBO = getPersonQuantityInfo(datePeriod[0], datePeriod[1]);
        getPersonInstantRateInfo(todayAnalysisBO);
        String[] sevenDaysDatePeriod = getDatePeriod(SystemConstant.PersonalMonitor.NEARLY_SEVEN_DAYS);
        PersonalStatictisAnalysisVO nearlySevenDaysAnalysisBO = getPersonQuantityInfo(sevenDaysDatePeriod[0],
            sevenDaysDatePeriod[1]);
        PersonalMonitorScreenVO bo = mergePersonMonitorInfo(todayAnalysisBO, nearlySevenDaysAnalysisBO);
        //人流监控离散图
        List<DiscreteFlowMonitorVO> discreteFlow = baseMapper
            .getMonitorDiscreteFlow(datePeriod[0] + SystemConstant.PersonalMonitor.ZERO_HOUR_SUFFIX, datePeriod[1]
                + SystemConstant.PersonalMonitor.LAST_HOUR_SUFFIX);
        bo.setDiscreteFlowMonitorVOS(discreteFlow);
        return bo;
    }

    private PersonalMonitorScreenVO mergePersonMonitorInfo(PersonalStatictisAnalysisVO todayAnalysisBO,
        PersonalStatictisAnalysisVO nearlySevenDaysAnalysisBO) {
        PersonalMonitorScreenVO bo = new PersonalMonitorScreenVO(todayAnalysisBO.getQuantity(),
            nearlySevenDaysAnalysisBO.getQuantity(), todayAnalysisBO.getInPersons(), todayAnalysisBO.getOutPersons(),
            nearlySevenDaysAnalysisBO.getInPersons(), nearlySevenDaysAnalysisBO.getOutPersons(),
            todayAnalysisBO.getClockInPersons(), todayAnalysisBO.getVisitors(),
            todayAnalysisBO.getInflowRate(), todayAnalysisBO.getOutflowRate());
        return bo;
    }

    private void getPersonInstantRateInfo(PersonalStatictisAnalysisVO bo) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime beforeMinute = localDateTime.minusSeconds(60);
        String beforeMinuteStr = SystemConstant.DateFormatInstant.dateTimeFormatter.format(beforeMinute);
        String localDateTimeStr = SystemConstant.DateFormatInstant.dateTimeFormatter.format(localDateTime);
        List<CommonBO> instants = baseMapper.instantRateSummary(beforeMinuteStr, localDateTimeStr);
        if (instants != null) {
            int size = instants.size();
            if (size > 0 && instants.get(0) != null && StringUtils.isNotBlank(instants.get(0).getSummary())) {
                bo.setInflowRate(Integer.valueOf(instants.get(0).getSummary()));
            }
            if (size > 1 && instants.get(1) != null && StringUtils.isNotBlank(instants.get(1).getSummary())) {
                bo.setOutflowRate(Integer.valueOf(instants.get(1).getSummary()));
            }
        }
    }

    private PersonalStatictisAnalysisVO getPersonQuantityInfo(String start, String end) {
        PersonalStatictisAnalysisVO bo = new PersonalStatictisAnalysisVO();
        List<CommonBO> commons = baseMapper.flowStatistics(start, end, SystemConstant.GroupName.DEFAULT_GROUP_NAME);
        if (commons != null) {
            int size = commons.size();
            if (size > 0 && commons.get(0) != null && StringUtils.isNotBlank(commons.get(0).getSummary())) {
                bo.setQuantity(Long.valueOf(commons.get(0).getSummary()));
            }
            if (size > 1 && commons.get(1) != null && StringUtils.isNotBlank(commons.get(1).getSummary())) {
                bo.setInPersons(Integer.valueOf(commons.get(1).getSummary()));
            }
            if (size > 2 && commons.get(2) != null && StringUtils.isNotBlank(commons.get(2).getSummary())) {
                bo.setOutPersons(Integer.valueOf(commons.get(2).getSummary()));
            }
            if (size > 3 && commons.get(3) != null && StringUtils.isNotBlank(commons.get(3).getSummary())) {
                bo.setClockInPersons(Integer.valueOf(commons.get(3).getSummary()));
            }
            if (size > 4 && commons.get(4) != null && StringUtils.isNotBlank(commons.get(4).getSummary())) {
                bo.setVisitors(Integer.valueOf(commons.get(4).getSummary()));
            }
            if (size > 5 && commons.get(5) != null && StringUtils.isNotBlank(commons.get(5).getSummary())) {
                bo.setAvgStandTime(Integer.valueOf(commons.get(5).getSummary()));
            }
        }
        return bo;
    }

    private String[] getDatePeriod(Integer dateType) {
        String[] rst = new String[2];
        LocalDate localDate = LocalDate.now();
        String beginDate = null;
        String endDate = null;
        switch (dateType) {
            case PersonalMonitor.YESTERDAY:
                beginDate = SystemConstant.DateFormatInstant.dateFormatter.format(localDate.minusDays(1));
                endDate = beginDate;
                break;
            case PersonalMonitor.TODAY:
                beginDate = SystemConstant.DateFormatInstant.dateFormatter.format(localDate);
                endDate = beginDate;
                break;
            case PersonalMonitor.NEARLY_SEVEN_DAYS:
                beginDate = SystemConstant.DateFormatInstant.dateFormatter.format(localDate.minusDays(7));
                endDate = SystemConstant.DateFormatInstant.dateFormatter.format(localDate);
                break;
            case PersonalMonitor.NEARLY_THIRTY_DAYS:
                beginDate = SystemConstant.DateFormatInstant.dateFormatter.format(localDate.minusDays(30));
                endDate = SystemConstant.DateFormatInstant.dateFormatter.format(localDate);
                break;
            default:
                break;
        }
        rst[0] = beginDate;
        rst[1] = endDate;
        return rst;
    }

}
