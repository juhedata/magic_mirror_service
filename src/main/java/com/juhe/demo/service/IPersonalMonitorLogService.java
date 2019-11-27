package com.juhe.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juhe.demo.bo.PersonalBO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.entity.PersonalMonitorLog;
import java.util.Calendar;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 人员监控信息 服务类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface IPersonalMonitorLogService extends IService<PersonalMonitorLog> {


    /**
     * @return java.util.List<com.juhe.demo.entity.PersonalInfo>
     * @Author xxmfypp
     * @Description 监控视频图片
     * @Date 16:39 2019/7/12
     * @Param [image：图片, action：动作,imageName 图片名称]
     **/
    @Transactional
    List<PersonalBO> monitorImage(String image, Long action, String imageName);

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 查询人员监控信息列表
     * @Date 14:48 2019/7/16
     * @Param [name, startTime, endTime, action, groupId, page, size]
     **/
    CommonResult listPersonMonitorInfo(String name, String startTime, String endTime, Integer action, Long groupId,
        Integer page, Integer size);

    /**
     * @return void
     * @Author kuai.zhang
     * @Description 探针识别mac
     * @Date 14:01 2019/8/14
     * @Param [mac]
     **/
    @Transactional
    void monitorMac(String mac);

    XSSFWorkbook exportAttendanceExcel(Calendar cal);
}
