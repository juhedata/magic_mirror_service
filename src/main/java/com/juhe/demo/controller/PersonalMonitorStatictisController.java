package com.juhe.demo.controller;


import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant.DateFormatInstant;
import com.juhe.demo.constant.SystemConstant.PersonalMonitor;
import com.juhe.demo.service.IPersonalMonitorStatictisService;
import com.juhe.demo.util.DateUtil;
import com.juhe.demo.vo.PersonalMonitorScreenVO;
import com.juhe.demo.vo.PersonalStatictisAnalysisVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 人员监控统计信息 前端控制器
 * </p>
 *
 * @author xuxm
 * @since 2019-07-15
 */
@Api(value = "人员监控统计API", tags = "人员监控统计API")
@RestController
@RequestMapping("/statistics")
@Validated
public class PersonalMonitorStatictisController {

    @Autowired
    private IPersonalMonitorStatictisService iPersonalMonitorStatictisService;

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author kuai.zhang
     * @Description 监控统计分析
     * @Date 11:33 2019/7/23
     * @Param [dateType, startDate, endDate] @Size(min = -1,max = 2,message = "日期类型不合法[-1:昨日,0:今日,1:近七天,2:近30天]")
     **/
    @ApiOperation(value = "监控统计分析[人流统计、流入流出人数、打卡人数、访客人数、平均停留时间]")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "date_type", value = "时间类型", allowableValues = "-1,0,1,2", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "start_date", value = "起始日期", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "end_date", value = "结束日期", dataType = "String", paramType = "query")
    })
    @GetMapping(value = "/persons")
    @PreAuthorize("hasAuthority('statistic')")
    public CommonResult summaryPersonalStatistic(@RequestParam(value = "date_type", required = false) Integer dateType,
        @RequestParam(value = "start_date", required = false) String startDate,
        @RequestParam(value = "end_date", required = false) String endDate) {
        boolean dateTypeValid = (dateType != null) && (PersonalMonitor.YESTERDAY == dateType ||
            PersonalMonitor.TODAY == dateType || PersonalMonitor.NEARLY_SEVEN_DAYS == dateType ||
            PersonalMonitor.NEARLY_THIRTY_DAYS == dateType);
        if (dateTypeValid || (!StringUtils.isAllBlank(startDate, endDate) && DateUtil
            .isValidDateTime(startDate, endDate, DateFormatInstant.simpleDateFormatter))) {
            PersonalStatictisAnalysisVO result = iPersonalMonitorStatictisService
                .statisticAnalysis(dateTypeValid, dateType, startDate, endDate);
            return CommonResult.success(result);
        } else {
            return CommonResult
                .success(new PersonalStatictisAnalysisVO(0L, 0, 0, 0, 0, 0, 0,
                    0, new ArrayList<>()), "时间类型或日期不合法");
        }
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author kuai.zhang
     * @Description 监控大屏
     * @Date 11:33 2019/7/23
     **/
    @ApiOperation(value = "监控大屏[近7天(人流统计、流入流出人数)、今日(打卡人数、访客人数、流入流出速率)]")
    @GetMapping(value = "/screen")
    public CommonResult monitorScreenStatistic() {
        PersonalMonitorScreenVO result = iPersonalMonitorStatictisService.monitorScreenStatistic();
        return CommonResult.success(result);
    }

}
