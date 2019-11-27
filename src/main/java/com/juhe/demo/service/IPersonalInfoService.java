package com.juhe.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juhe.demo.entity.PersonalInfo;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 人员信息 服务类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface IPersonalInfoService extends IService<PersonalInfo> {

    /**
     * @return boolean
     * @Author xxmfypp
     * @Description 批量保存数据并注册到百度人脸库
     * @Date 16:41 2019/7/11
     * @Param []
     **/
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ, timeout = 60)
    Map<String, Integer> savePersonalAndRegisterToBaidu(List<PersonalInfo> personalInfoList) throws Exception;

    /**
     * @return void
     * @Author xxmfypp
     * @Description 批量删除人员信息并删除百度人脸库
     * @Date 10:28 2019/7/12
     * @Param [personalIdList]
     **/
    @Transactional
    void removePersonalAndRemoveFormBaidu(List<Long> personalIdList) throws Exception;

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 获取员工信息列表
     * @Date 19:19 2019/7/16
     * @Param [name, gender, employeeNo, groupId, page, size]
     **/
    IPage<PersonalInfo> listPersonInfo(String name, Integer gender, String employeeNo, Long groupId, Integer page,
        Integer size);

    /**
     * @return void
     * @Author kuai.zhang
     * @Description 删除人员相关图片与mac数据
     * @Date 13:46 2019/7/30
     * @Param [personalId]
     **/
    @Transactional
    void removePersonalImageAndMacs(Long personalId);
}
