package com.juhe.demo.controller;

import com.baidu.aip.util.Base64Util;
import com.juhe.demo.ao.MonitorConditionAO;
import com.juhe.demo.bo.PersonalBO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant.Common;
import com.juhe.demo.constant.SystemConstant.Personal;
import com.juhe.demo.service.IPersonalMonitorLogService;
import com.juhe.demo.util.CommonUtil;
import com.juhe.demo.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @CLassName AnalyzeController
 * @Description 图像识别API
 * @Author xxmfypp
 * @Date 2019/7/12 16:13
 * @Version 1.0
 **/
@RestController
@RequestMapping("/analyze")
@Scope("prototype")
@Api(value = "图像识别API", tags = "图像识别API")
@Slf4j
@Validated
public class AnalyzeController {

    @Value("${personal.icon.path}")
    private String savePath;

    @Autowired
    private IPersonalMonitorLogService iPersonalMonitorLogService;

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 监控人脸
     * @Date 16:25 2019/7/12
     * @Param [image：人脸照片 Base64 编码, action：动作 0表示入 1表示出]
     **/
    @ApiOperation(value = "监控人脸")
    @PostMapping(value = "/monitor")
    public CommonResult checkIn(@Validated @RequestBody MonitorConditionAO conditionBo, BindingResult result) {
        long t = System.currentTimeMillis();
        String image = conditionBo.getImage();
        Long action = conditionBo.getAction();
        if (!(action.equals(1L) || action.equals(0L))) {
            return CommonResult.failed("动作不能为空，且必须为0或1");
        }
        //存储大屏截图
        String imageName = saveMonitorImage(image);
        List<PersonalBO> data = iPersonalMonitorLogService.monitorImage(image, action, imageName);
        log.info("监控消耗时间: " + (System.currentTimeMillis() - t));
        return CommonResult.success(data);
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author kuai.zhang
     * @Description 探针识别签到
     * @Date 13:40 2019/8/14
     * @Param [mac]
     **/
    @ApiOperation(value = "探针识别")
    @PostMapping(value = "/probe")
    public CommonResult probeCheckIn(@RequestBody String data) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(data)) {
            return CommonResult.failed("MAC地址不存在");
        }
        log.debug("mac data: " + URLDecoder.decode(data, Common.URL_ENCODE));
        List<String> macs = Arrays.stream(URLDecoder.decode(data, Common.URL_ENCODE).split("\n"))
            .filter(val -> StringUtils.isNotBlank(val) && val.contains("|")).map(val -> val.split("\\|")[1])
            .collect(Collectors.toList());
        if (macs == null || macs.isEmpty()) {
            return CommonResult.failed("MAC地址不存在");
        }
        String mac = StringUtils.join(macs, ",");
        if (!CommonUtil.validPersonMac(mac)) {
            return CommonResult.failed("MAC地址不合法");
        }
        iPersonalMonitorLogService.monitorMac(mac);
        return CommonResult.success();
    }

    @ApiOperation("下载人员图片")
    @GetMapping(value = "/download/{image}")
    public void downloadImage(@NotBlank(message = "图片不能为空") @PathVariable(value = "image") String image,
        HttpServletResponse response) {
        File file = new File(FileUtil.getCompressImageName(savePath, image));
        if (!file.exists()) {
            file = new File(savePath + File.separator + image);
            if (!file.exists()) {
                log.error("图片不存在");
            } else {
                try {
                    Thumbnails.of(file).scale(Personal.IMAGE_SCALE).outputQuality(Personal.IMAGE_QUALITY)
                        .toFile(FileUtil.getCompressImageName(savePath, image));
                } catch (IOException e) {
                    log.error("compress image failed");
                }
            }
        }
        try (FileInputStream inputStream = new FileInputStream(file); OutputStream outputStream = response
            .getOutputStream()) {
            byte[] data = new byte[(int) file.length()];
            inputStream.read(data);
            response.setContentType("image/png");
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            log.error("图片读取失败");
            e.printStackTrace();
        }
    }

    private String saveMonitorImage(String image) {
        byte[] bytes = Base64Util.decode(image);
        String imageName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        File saveFilePath = new File(savePath + File.separator + "monitor");
        if (!saveFilePath.exists()) {
            saveFilePath.mkdirs();
        }
        saveFilePath = new File(saveFilePath.getPath() + File.separator + imageName);
        try (
            FileOutputStream fileOutputStream = new FileOutputStream(saveFilePath);) {
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (IOException e) {
            log.error("save monitor failed");
        }
        return imageName;
    }

}
