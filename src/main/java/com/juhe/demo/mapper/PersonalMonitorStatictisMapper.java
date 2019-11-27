package com.juhe.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.juhe.demo.entity.PersonalMonitorStatictis;
import com.juhe.demo.transfer.CommonBO;
import com.juhe.demo.transfer.PersonMonitorConditionBO;
import com.juhe.demo.vo.DiscreteFlowMonitorVO;
import com.juhe.demo.vo.DiscreteFlowNumVO;
import com.juhe.demo.vo.PersonMonitorSummaryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 人员监控统计信息 Mapper 接口
 * </p>
 *
 * @author xuxm
 * @since 2019-07-15
 */
public interface PersonalMonitorStatictisMapper extends BaseMapper<PersonalMonitorStatictis> {

    /**
     * @return com.juhe.demo.vo.PersonMonitorSummaryVO
     * @Author kuai.zhang
     * @Description 监控信息统计
     * @Date 14:59 2019/7/23
     * @Param [condition]
     **/
    PersonMonitorSummaryVO summaryPersonMonitorInfo(@Param(value = "condition") PersonMonitorConditionBO condition);

    /**
     * @return java.util.List<CommonBO>
     * @Author kuai.zhang
     * @Description 人流统计
     * @Date 14:59 2019/7/23
     * @Param [start, end]
     **/
    List<CommonBO> flowStatistics(@Param(value = "start") String start, @Param(value = "end") String end,
                                  @Param(value = "groupName") String groupName);

    /**
     * @return java.util.List<CommonBO>
     * @Author kuai.zhang
     * @Description 实时速率统计
     * @Date 17:31 2019/7/23
     * @Param [beforeMinuteStr, localDateTimeStr]
     **/
    List<CommonBO> instantRateSummary(@Param(value = "start") String beforeMinuteStr,
                                      @Param(value = "end") String localDateTimeStr);

    /**
     * @return java.util.List<com.juhe.demo.vo.DiscreteFlowNumVO>
     * @Author kuai.zhang
     * @Description 日期分组统计
     * @Date 17:56 2019/7/23
     * @Param [start, end]
     **/
    List<DiscreteFlowNumVO> discreteDateFlowNum(@Param(value = "start") String start, @Param(value = "end") String end);

    /**
     * @return java.util.List<com.juhe.demo.vo.DiscreteFlowNumVO>
     * @Author kuai.zhang
     * @Description 时间In分组统计
     * @Date 17:56 2019/7/23
     * @Param [start, end]
     **/
    List<DiscreteFlowNumVO> discreteTimeInFlowNum(@Param(value = "start") String start,
                                                  @Param(value = "end") String end);

    /**
     * @return java.util.List<com.juhe.demo.vo.DiscreteFlowNumVO>
     * @Author kuai.zhang
     * @Description 时间Out分组统计
     * @Date 17:56 2019/7/23
     * @Param [start, end]
     **/
    List<DiscreteFlowNumVO> discreteTimeOutFlowNum(@Param(value = "start") String start,
                                                   @Param(value = "end") String end);

    /**
     * @return java.util.List<com.juhe.demo.vo.DiscreteFlowMonitorVO>
     * @Author kuai.zhang
     * @Description 获取大屏监控人流离散信息
     * @Date 9:57 2019/7/24
     * @Param [start, end]
     **/
    List<DiscreteFlowMonitorVO> getMonitorDiscreteFlow(@Param(value = "start") String start,
                                                       @Param(value = "end") String end);
}
