package com.juhe.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.juhe.demo.ao.PersonalAO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant;
import com.juhe.demo.constant.SystemConstant.Common;
import com.juhe.demo.constant.SystemConstant.Personal;
import com.juhe.demo.entity.GroupName;
import com.juhe.demo.entity.PersonalIcon;
import com.juhe.demo.entity.PersonalInfo;
import com.juhe.demo.entity.PersonalMac;
import com.juhe.demo.service.IGroupNameService;
import com.juhe.demo.service.IPersonalIconService;
import com.juhe.demo.service.IPersonalInfoService;
import com.juhe.demo.service.IPersonalMacService;
import com.juhe.demo.util.CommonUtil;
import com.juhe.demo.util.ExcelUtils;
import com.juhe.demo.vo.PersonalIconVO;
import com.juhe.demo.vo.PersonalInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @CLassName PersonalInfoController
 * @Description 人员管理相关API
 * @Author xxmfypp
 * @Date 2019/7/5 14:32
 * @Version 1.0
 **/
@RestController
@RequestMapping("/")
@Api(value = "人员管理相关API", tags = "人员管理相关API")
@Slf4j
@Validated
public class PersonalInfoController {

    @Value("${personal.icon.path}")
    private String savePath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private IPersonalInfoService iPersonalInfoService;

    @Autowired
    private IPersonalIconService iPersonalIconService;

    @Autowired
    private IPersonalMacService iPersonalMacService;

    @Autowired
    private IGroupNameService iGroupNameService;

    @ApiOperation(value = "人员组集合")
    @GetMapping(value = "person/group")
    public CommonResult personGroup() {
        List<GroupName> groupNameList = iGroupNameService.list();
        Map<String, String> groupNames = groupNameList.stream()
            .collect(Collectors.toMap(key -> String.valueOf(key.getId()), value -> value.getName()));
        return CommonResult.success(groupNames);
    }

    @ApiOperation(value = "人员列表", notes = "分页查询")
    @PreAuthorize("hasAuthority('personinfo')")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "姓名", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "gender", value = "性别", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "employee_no", value = "工号", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "group_id", value = "分组编号", dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "page", required = true, value = "第几页", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "size", required = true, value = "每页条数", dataType = "int", paramType = "query")
    })
    @GetMapping(value = "persons")
    public CommonResult listPersonInfo(@RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "gender", required = false) Integer gender,
        @RequestParam(value = "employee_no", required = false) String employeeNo,
        @RequestParam(value = "group_id", required = false) Long groupId,
        @NotNull(message = "页数不能为空") @RequestParam(value = "page", defaultValue = "1") Integer page,
        @NotNull(message = "每页条数不能为空") @RequestParam(value = "size", defaultValue = "10") Integer size) {
        IPage<PersonalInfo> personalInfoIPage = iPersonalInfoService
            .listPersonInfo(name, gender, employeeNo, groupId, page, size);
        JSONObject json = new com.alibaba.fastjson.JSONObject();
        List<PersonalInfoVO> vos = personalInfoIPage.getRecords().stream().map(val -> {
            PersonalInfoVO vo = new PersonalInfoVO();
            BeanUtils.copyProperties(val, vo);
            return vo;
        }).collect(Collectors.toList());
        json.put("count", personalInfoIPage.getTotal());
        json.put("records", vos);
        return CommonResult.success(json);
    }

    /**
     * @return void
     * @Author xxmfypp
     * @Description 下载人员录入模板
     * @Date 11:39 2019/7/17
     * @Param [response]
     **/
    @ApiOperation(value = "下载人员录入模板", notes = "swagger不支持excel文件下载", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @GetMapping(value = "person/template")
    @PreAuthorize("hasAuthority('personinfo')")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        InputStream fis = null;
        ServletOutputStream out = null;
        try {
            StringBuffer filePathBuffer = new StringBuffer(ResourceUtils.getURL("classpath:").getPath());
            filePathBuffer.append("template").append(File.separator).append(SystemConstant.Personal.TEMPLATE_NAME);
            String fileName = SystemConstant.Personal.TEMPLATE_NAME;
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                fileName = new String((fileName).getBytes("UTF-8"), "ISO-8859-1");
            }
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/binary;charset=UTF-8");
            fis = new FileInputStream(filePathBuffer.toString());
            out = response.getOutputStream();
            int b;
            byte[] buffer = new byte[1024];
            while ((b = fis.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
            out.flush();
        } catch (IOException e) {
            log.error("下载人员录入模板失败," + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description Excel导入上传人员信息
     * @Date 9:55 2019/7/12
     * @Param [file]
     **/
    @ApiOperation(value = "Excel导入上传人员信息", notes = "上传的Excel文件需要严格按照模板格式，否则会出现异常")
    @PostMapping(value = "persons/upload", headers = "content-type=multipart/form-data")
    @PreAuthorize("hasAuthority('personinfo')")
    public CommonResult uploadPersonalInfo(@ApiParam(value = "文件", required = true)
    @NotNull(message = "上传文件不能为空") @RequestParam(value = "file") MultipartFile file) {
        String fileType = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        if (!(Personal.EXCEL_XLS.equals(fileType) || Personal.EXCEL_XLSX.equals(fileType))) {
            return CommonResult.failed("文件类型不合法.");
        }
        try {
            List<PersonalAO> personalAOList = ExcelUtils.getPersonalInfoFromExcel(file, fileType, savePath);
            List<PersonalInfo> personalInfoList = convertPersonalInfos(personalAOList);
            if (personalInfoList == null || personalInfoList.isEmpty()) {
                return CommonResult.failed("文件中不存在合法数据");
            }
            Map<String, Integer> rst = iPersonalInfoService.savePersonalAndRegisterToBaidu(personalInfoList);
            String backMessage = "导入人员信息成功";
            if (!rst.isEmpty()) {
                StringBuffer buffer = new StringBuffer();
                rst.entrySet().stream().forEach(
                    entry -> buffer.append(entry.getKey()).append("存在").append(entry.getValue()).append("张未识别图片")
                        .append(" "));
                backMessage = buffer.toString();
                return CommonResult.failed(backMessage);
            }
            return CommonResult.successPost(backMessage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("批量保存人员信息失败:", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * @return 人员信息
     * @Author xxmfypp
     * @Description 根据人员id获取相关信息
     * @Date 11:39 2019/7/17
     * @Param personalId
     **/
    @ApiOperation(value = "根据人员id获取相关信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "人员id", dataType = "Long", required = true, paramType = "path")
    })
    @PreAuthorize("hasAuthority('personinfo')")
    @GetMapping(value = "person/{id}")
    public CommonResult getPersonInfo(
        @NotNull(message = "人员id不能为空") @PathVariable(value = "id") Long personalId) {
        PersonalInfo personalInfo = iPersonalInfoService.getById(personalId);
        if (personalInfo == null) {
            return CommonResult.failed("人员不存在");
        }
        personalInfo.setIcon(contextPath + Personal.IMAGE_PATH + personalInfo.getIcon());
        LambdaQueryWrapper<PersonalIcon> iconQueryWrapper = new LambdaQueryWrapper<>();
        iconQueryWrapper.eq(PersonalIcon::getPersonalId, personalId);
        List<PersonalIconVO> icons = iPersonalIconService.list(iconQueryWrapper).stream()
            .map(personalIcon -> {
                PersonalIconVO vo = new PersonalIconVO();
                personalIcon.setIcon(contextPath + Personal.IMAGE_PATH + personalIcon.getIcon());
                BeanUtils.copyProperties(personalIcon, vo);
                return vo;
            }).collect(Collectors.toList());
        LambdaQueryWrapper<PersonalMac> macQueryWrapper = new LambdaQueryWrapper<>();
        macQueryWrapper.eq(PersonalMac::getPersonalId, personalId);
        List<String> macs = iPersonalMacService.list(macQueryWrapper).stream().map(val -> val.getMac().toUpperCase())
            .collect(Collectors.toList());
        PersonalInfoVO vo = new PersonalInfoVO();
        BeanUtils.copyProperties(personalInfo, vo);
        vo.setIcons(icons);
        if (macs != null && !macs.isEmpty()) {
            vo.setMac(StringUtils.join(macs, Common.DELIMITER));
        }
        return CommonResult.success(vo);
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 保存人员信息 headers = "content-type=multipart/form-data"
     * @Date 9:55 2019/7/12
     * @Param [image, personalAO]
     **/
    @ApiOperation(value = "单个保存人员信息", notes = "swagger不支持多图片上传功能,单图片格式仅支持png和jpg")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "name", value = "姓名", dataType = "String", required = true, paramType = "query"),
        @ApiImplicitParam(name = "group_id", value = "组id", dataType = "Long", required = true, paramType = "query"),
        @ApiImplicitParam(name = "gender", value = "性别", dataType = "int", allowableValues = "0,1", required = true, paramType = "query"),
        @ApiImplicitParam(name = "birthday", value = "生日", dataType = "String", required = true, paramType = "query"),
        @ApiImplicitParam(name = "employee_no", value = "工号", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "title", value = "职称", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "department", value = "部门", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "company", value = "公司", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "mac", value = "MAC地址", dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "note", value = "备注", dataType = "String", paramType = "query")
    })
    @PreAuthorize("hasAuthority('personinfo')")
    @PostMapping(value = "person")
    public CommonResult savePersonInfo(
        @NotNull(message = "头像图片不能为空") @RequestPart(value = "icon") MultipartFile icon,
        @NotNull(message = "人脸图片不能为空") @RequestPart(value = "images[]") MultipartFile[] images,
        @RequestParam(value = "id", required = false) Long id,
        @NotBlank(message = "人员名称不能为空") @Length(max = 32, message = "姓名长度不能超过32") @RequestParam(value = "name") String name,
        @NotNull(message = "组显示id不能为空") @RequestParam(value = "group_id") Long groupId,
        @NotNull(message = "性别不能为空") @Range(max = 1, message = "性别只能为0或1") @RequestParam(value = "gender") Integer gender,
        @NotBlank(message = "生日不能为空") @Pattern(regexp = "[0-9]{4}-[0-9]{2}-[0-9]{2}", message = "生日不合法") @RequestParam(value = "birthday") String birthday,
        @RequestParam(value = "employee_no", required = false) String employeeNo,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "department", required = false) String department,
        @RequestParam(value = "company", required = false) String company,
        @RequestParam(value = "mac", required = false) String mac,
        @RequestParam(value = "note", required = false) String note) {
        long t = System.currentTimeMillis();
        //编辑
        if (id != null) {
            int c = iPersonalInfoService.count(
                new LambdaQueryWrapper<PersonalInfo>().eq(PersonalInfo::getDeleted, 0).eq(PersonalInfo::getId, id));
            if (c == 0) {
                return CommonResult.failed("人员不存在.");
            }
        }
        LambdaQueryWrapper<PersonalInfo> existPersonQuery = new LambdaQueryWrapper<>();
        existPersonQuery.eq(PersonalInfo::getDeleted, 0).eq(PersonalInfo::getName, name);
        if (id != null) {
            existPersonQuery.ne(PersonalInfo::getId, id);
        }
        int count = iPersonalInfoService.count(existPersonQuery);
        if (count > 0) {
            return CommonResult.failed("人员名称已存在.");
        }
        //头像图片验证
        List<MultipartFile> faces = validPersonImages(new MultipartFile[]{icon});
        if (faces == null || faces.isEmpty()) {
            return CommonResult.failed("头像图片不存在或格式不合法.");
        }
        //人脸图片验证
        List<MultipartFile> validImages = validPersonImages(images);
        if (validImages == null || validImages.isEmpty()) {
            return CommonResult.failed("人脸图片不存在或格式不合法.");
        }
        List<String> macList = null;
        if (StringUtils.isNotBlank(mac)) {
            if (!CommonUtil.validPersonMac(mac)) {
                return CommonResult.failed("人员MAC不合法.");
            }
            macList = Arrays.asList(mac.split(Common.DELIMITER));
        }
        //保存头像图片
        String[] iconNames = saveImages(faces, savePath);
        //保存人脸图片
        String[] imageNames = saveImages(validImages, savePath);
        PersonalAO personalAO = new PersonalAO(id, name, groupId, gender, birthday, employeeNo, title, department,
            company, note);
        PersonalInfo personalInfo = new PersonalInfo();
        BeanUtils.copyProperties(personalAO, personalInfo);
        //编辑
        if (personalInfo.getId() != null) {
            PersonalInfo originPerson = iPersonalInfoService.getById(personalInfo.getId());
            if (originPerson != null) {
                personalInfo.setCreateTime(originPerson.getCreateTime());
                personalInfo.setId(originPerson.getId());
                //删除原有图片与mac地址
                iPersonalInfoService.removePersonalImageAndMacs(originPerson.getId());
            }
        } else {
            personalInfo.setCreateTime(LocalDateTime.now());
        }
        List<PersonalIcon> personalIcons = Arrays.stream(imageNames).map(image -> new PersonalIcon(image, null, null))
            .collect(Collectors.toList());
        if (macList != null && !macList.isEmpty()) {
            List<PersonalMac> personalMacs = macList.stream().map(val -> new PersonalMac(val.toUpperCase(), null))
                .collect(Collectors.toList());
            personalInfo.setMacs(personalMacs);
        }
        personalInfo.setIcon((iconNames == null || iconNames.length == 0) ? "" : iconNames[0]);
        personalInfo.setIcons(personalIcons);
        personalInfo.setBirthday(LocalDate.parse(birthday));
        personalInfo.setDeleted(SystemConstant.Personal.UNDELETE);
        List<PersonalInfo> personalInfoList = new ArrayList<>();
        personalInfoList.add(personalInfo);
        try {
            Map<String, Integer> rst = iPersonalInfoService.savePersonalAndRegisterToBaidu(personalInfoList);
            String backMessage = "导入人员信息成功";
            if (!rst.isEmpty()) {
                Integer unValidCount = new ArrayList<>(rst.entrySet()).get(0).getValue();
                backMessage = "该人员存在" + unValidCount + "张未识别图片";
                return CommonResult.failed(backMessage);
            }
            return CommonResult.successPost(backMessage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存人员信息失败:", e);
            return CommonResult.failed(e.getMessage());
        } finally {
            log.debug("保存用户消耗总时间:" + (System.currentTimeMillis() - t));
        }
    }

    private List<MultipartFile> validPersonImages(MultipartFile[] images) {
        List<MultipartFile> rst = new ArrayList<>();
        if (images.length == 0) {
            return null;
        }
        String fileType;
        for (MultipartFile image : images) {
            fileType = StringUtils.substringAfterLast(image.getOriginalFilename(), ".");
            if (!Personal.IMAGE_TYPES.contains(fileType.toLowerCase())) {
                return null;
            }
            if (!image.isEmpty()) {
                rst.add(image);
            }
        }
        return rst;
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description[单个修改+批量修改,以逗号分隔]
     * @Date 9:59 2019/7/12
     * @Param [personalIds：人员信息编号, groupNameId：分组编号]
     **/
    @ApiOperation(value = "修改人员信息分组", notes = "批量修改分组信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ids", value = "人员信息编号,人员编号多个用,号隔开", required = true, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "group_id", value = "分组编号", required = true, dataType = "Long", paramType = "query")
    })
    @PreAuthorize("hasAuthority('personinfo')")
    @PatchMapping(value = "persons/group")
    public CommonResult changeGroupName(
        @NotBlank(message = "人员编号不能为空") @RequestParam(value = "ids") String personalIds,
        @NotNull(message = "分组编号不能为空") @RequestParam(value = "group_id") Long groupNameId) {
        String[] personalIdArr = personalIds.split(Common.DELIMITER);
        if (personalIdArr == null || personalIdArr.length == 0) {
            return CommonResult.failed("人员编号不能为空");
        }
        GroupName groupName = iGroupNameService.getById(groupNameId);
        if (groupName == null) {
            return CommonResult.failed("不存在对应的分组编号");
        }
        for (String personalId : personalIdArr) {
            PersonalInfo personalInfo = iPersonalInfoService.getById(personalId);
            if (personalInfo == null) {
                return CommonResult.failed("不存在对应的人员编号:" + personalId);
            }
            personalInfo.setGroupId(groupNameId);
            iPersonalInfoService.updateById(personalInfo);
        }
        return CommonResult.success("修改分组成功");
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 删除人员信息
     * @Date 10:16 2019/7/12
     * @Param [personalIds：人员编号  多个用,号隔开]
     **/
    @ApiOperation(value = "删除人员信息", notes = "批量删除人员信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ids", value = "人员信息编号列表,人员编号多个用,号隔开", required = true, dataType = "String", paramType = "query")
    })
    @PreAuthorize("hasAuthority('personinfo')")
    @DeleteMapping(value = "persons")
    public CommonResult deletePersonalInfo(
        @NotBlank(message = "人员编号不能为空") @RequestParam(value = "ids") String personalIds) {
        String[] personalIdArray = personalIds.split(Common.DELIMITER);
        List<Long> personalIdList = Arrays.stream(personalIdArray).map(personalId -> Long.valueOf(personalId))
            .collect(Collectors.toList());
        try {
            iPersonalInfoService.removePersonalAndRemoveFormBaidu(personalIdList);
            return CommonResult.successDelete("删除人员信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("批量保存人员信息失败:", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * @return java.lang.String[]
     * @Author kuai.zhang
     * @Description 保存图片集合
     * @Date 10:56 2019/7/30
     * @Param [images, savePath]
     **/
    private String[] saveImages(List<MultipartFile> images, String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        int len = images.size();
        String[] rst = new String[len];
        MultipartFile image;
        String picName;
        String fileType;
        List<String> pictureNames = new ArrayList<>();
        List<byte[]> pictures = new ArrayList<>();
        try {
            for (int i = 0; i < len; i++) {
                image = images.get(i);
                // 获取图片名称
                picName = UUID.randomUUID().toString().replace("-", "");
                fileType = StringUtils.substringAfterLast(image.getOriginalFilename(), ".");
                rst[i] = picName + "." + fileType;
                pictureNames.add(rst[i]);
                pictures.add(image.getBytes());
            }
            ExcelUtils.printImg(pictures, pictureNames, savePath);
        } catch (IOException e) {
            log.error("get image failed," + e.getMessage());
        }
        return rst;
    }

    /**
     * @return java.util.List<com.juhe.demo.entity.PersonalInfo>
     * @Author xxmfypp
     * @Description personalAOList 转换成 personalInfoList
     * @Date 16:36 2019/7/11
     * @Param [personalAOList]
     **/
    private List<PersonalInfo> convertPersonalInfos(List<PersonalAO> personalAOList) {
        List<PersonalInfo> personalInfoList = new ArrayList<>();
        List<GroupName> groupNameList = iGroupNameService.list();
        Map<String, Long> groupMap = groupNameList.stream()
            .collect(Collectors.toMap(key -> key.getName(), val -> val.getId()));
        LambdaQueryWrapper<PersonalInfo> queryWrapper;
        PersonalInfo originPerson;
        for (PersonalAO personalAO : personalAOList) {
            PersonalInfo personalInfo = new PersonalInfo();
            Long groupId = groupMap.get(personalAO.getGroupName());
            if (groupId != null) {
                String name = personalAO.getName();
                queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(PersonalInfo::getDeleted, 0).eq(PersonalInfo::getName, name);
                originPerson = iPersonalInfoService.getOne(queryWrapper);
                BeanUtils.copyProperties(personalAO, personalInfo);
                personalInfo.setBirthday(LocalDate.parse(personalAO.getBirthday()));
                personalInfo.setGroupId(groupId);
                if (originPerson != null) {
                    personalInfo.setId(originPerson.getId());
                    personalInfo.setCreateTime(originPerson.getCreateTime());
                    //删除旧的图片与mac地址
                    iPersonalInfoService.removePersonalImageAndMacs(personalInfo.getId());
                } else {
                    personalInfo.setCreateTime(LocalDateTime.now());
                }
                personalInfoList.add(personalInfo);
            }
        }
        return personalInfoList;
    }

}
