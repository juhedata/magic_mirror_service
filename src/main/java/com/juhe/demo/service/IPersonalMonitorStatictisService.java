package com.juhe.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juhe.demo.entity.PersonalMonitorStatictis;
import com.juhe.demo.transfer.PersonMonitorConditionBO;
import com.juhe.demo.vo.PersonMonitorSummaryVO;
import com.juhe.demo.vo.PersonalMonitorScreenVO;
import com.juhe.demo.vo.PersonalStatictisAnalysisVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 人员监控统计信息 服务类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-15
 */
public interface IPersonalMonitorStatictisService extends IService<PersonalMonitorStatictis> {

    /**
     * @return void
     * @Author xxmfypp
     * @Description //统计人员监控数据
     * @Date 11:15 2019/7/16
     * @Param [userId] 人员id
     **/
    @Transactional
    void statisticPersonMonitorData(Long userId, Integer action);

    /**
     * @return com.juhe.demo.vo.PersonMonitorSummaryVO
     * @Author xxmfypp
     * @Description 统计人员监控信息
     * @Date 16:48 2019/7/16
     * @Param [condition]
     **/
    PersonMonitorSummaryVO summaryPersonMonitorInfo(PersonMonitorConditionBO condition);

    /**
     * @return com.juhe.demo.vo.PersonalStatictisAnalysisVO
     * @Author kuai.zhang
     * @Description 统计分析
     * @Date 14:08 2019/7/23
     * @Param [dateTypeValid, dateType, startDate, endDate]
     **/
    PersonalStatictisAnalysisVO statisticAnalysis(Boolean dateTypeValid, Integer dateType, String startDate,
        String endDate);

    /**
     * @return com.juhe.demo.vo.PersonMonitorInfoVO
     * @Author kuai.zhang
     * @Description 监控大屏分析
     * @Date 20:22 2019/7/23
     * @Param []
     **/
    PersonalMonitorScreenVO monitorScreenStatistic();
}
