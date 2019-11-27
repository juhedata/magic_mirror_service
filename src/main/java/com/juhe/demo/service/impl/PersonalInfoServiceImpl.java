package com.juhe.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juhe.demo.api.ArcFaceAnalysis;
import com.juhe.demo.config.ArcFacePool;
import com.juhe.demo.constant.SystemConstant.Personal;
import com.juhe.demo.entity.PersonalIcon;
import com.juhe.demo.entity.PersonalInfo;
import com.juhe.demo.entity.PersonalMac;
import com.juhe.demo.mapper.PersonalInfoMapper;
import com.juhe.demo.service.IPersonalIconService;
import com.juhe.demo.service.IPersonalInfoService;
import com.juhe.demo.service.IPersonalMacService;
import com.juhe.demo.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 人员信息 服务实现类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Service
@Slf4j
public class PersonalInfoServiceImpl extends ServiceImpl<PersonalInfoMapper, PersonalInfo> implements
    IPersonalInfoService {

    @Autowired
    private IPersonalIconService iPersonalIconService;

    @Autowired
    private IPersonalMacService iPersonalMacService;

    @Value("${personal.icon.path}")
    private String savePath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private ArcFacePool arcFacePool;

    @Override
    public Map<String, Integer> savePersonalAndRegisterToBaidu(List<PersonalInfo> personalInfoList) {
        long t = System.currentTimeMillis();
        Map<String, Integer> rst = new HashMap<>(8);
        for (PersonalInfo personalInfo : personalInfoList) {
            saveOrUpdate(personalInfo);
            List<PersonalIcon> icons = personalInfo.getIcons();
            icons.stream().forEach(icon -> icon.setPersonalId(personalInfo.getId()));
            List<PersonalMac> macs = personalInfo.getMacs();
            if (macs != null) {
                macs.stream().forEach(mac -> mac.setPersonalId(personalInfo.getId()));
                iPersonalMacService.saveBatch(macs);
            }
            long t1 = System.currentTimeMillis();
            List<String> unValidIcons = new ArrayList<>();
            ArcFaceAnalysis arcFaceAnalysis = null;
            try {
                arcFaceAnalysis = arcFacePool.borrowObject();
                ArcFaceAnalysis finalArcFaceAnalysis = arcFaceAnalysis;
                icons.forEach(personalIcon -> {
                    File f = new File(savePath + File.separator + personalIcon.getIcon());
                    byte[] feature = finalArcFaceAnalysis.getFileFaceFeature(f);
                    if (feature == null || feature.length == 0) {
                        unValidIcons.add(personalIcon.getIcon());
                        log.error("注册ArcFace人脸库异常");
                    } else {
                        personalIcon.setFeature(feature);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                arcFacePool.returnObject(arcFaceAnalysis);
            }
            log.debug("注册ArcFace花费时间:" + (System.currentTimeMillis() - t1));
            icons = icons.stream().filter(val -> !unValidIcons.contains(val.getIcon()))
                .collect(Collectors.toList());
            iPersonalIconService.saveBatch(icons);
            if (!unValidIcons.isEmpty()) {
                rst.put(personalInfo.getName(), unValidIcons.size());
                //删除无效的图片
                unValidIcons.parallelStream().filter(val -> !val.equals(personalInfo.getIcon()))
                    .forEach(val -> FileUtil.deleteImage(savePath, val));
            }
        }
        log.debug("新增/编辑用户->ArcFace花费时间:" + (System.currentTimeMillis() - t));
        return rst;
    }

    @Override
    public void removePersonalAndRemoveFormBaidu(List<Long> personalIdList) {
        PersonalInfo personalInfo;
        for (Long id : personalIdList) {
            personalInfo = getById(id);
            if (personalInfo != null) {
                personalInfo.setDeleted(1);
                updateById(personalInfo);
                removePersonalImageAndMacs(id);
            }
        }
    }

    @Override
    public void removePersonalImageAndMacs(Long personalId) {
        PersonalInfo personalInfo = getById(personalId);
        //删除原有图片与mac地址
        LambdaQueryWrapper<PersonalIcon> iconQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<PersonalMac> macQueryWrapper = new LambdaQueryWrapper<>();
        iconQueryWrapper.eq(PersonalIcon::getPersonalId, personalId);
        macQueryWrapper.eq(PersonalMac::getPersonalId, personalId);
        iPersonalIconService.list(iconQueryWrapper).parallelStream()
            .forEach(icon -> FileUtil.deleteImage(savePath, icon.getIcon()));
        if (personalInfo != null && StringUtils.isNotBlank(personalInfo.getIcon())) {
            FileUtil.deleteImage(savePath, personalInfo.getIcon());
        }
        iPersonalIconService.remove(iconQueryWrapper);
        iPersonalMacService.remove(macQueryWrapper);
    }

    @Override
    public IPage<PersonalInfo> listPersonInfo(String name, Integer gender, String employeeNo, Long groupId,
        Integer page, Integer size) {
        LambdaQueryWrapper<PersonalInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(PersonalInfo::getName, name);
        }
        if (gender != null && gender != -1) {
            queryWrapper.eq(PersonalInfo::getGender, gender);
        }
        if (StringUtils.isNotBlank(employeeNo)) {
            queryWrapper.eq(PersonalInfo::getEmployeeNo, employeeNo);
        }
        if (groupId != null && groupId != -1) {
            queryWrapper.eq(PersonalInfo::getGroupId, groupId);
        }
        queryWrapper.eq(PersonalInfo::getDeleted, 0);
        IPage<PersonalInfo> iPage = page(new Page<>(page, size), queryWrapper);
        iPage.getRecords().forEach(record -> {
            record.setIcon(contextPath + Personal.IMAGE_PATH + record.getIcon());
        });
        return iPage;
    }
}
