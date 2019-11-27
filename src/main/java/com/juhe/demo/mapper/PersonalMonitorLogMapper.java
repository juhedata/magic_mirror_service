package com.juhe.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.juhe.demo.entity.PersonalMonitorLog;
import com.juhe.demo.transfer.PersonMonitorConditionBO;
import com.juhe.demo.vo.PersonMonitorInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 人员监控信息 Mapper 接口
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface PersonalMonitorLogMapper extends BaseMapper<PersonalMonitorLog> {

    Long countPersonMonitorInfo(@Param(value = "condition") PersonMonitorConditionBO condition);

    List<PersonMonitorInfoVO> listPersonMonitorInfo(Pagination page,
                                                    @Param(value = "condition") PersonMonitorConditionBO condition);


}
