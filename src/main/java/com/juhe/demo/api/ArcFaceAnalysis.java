package com.juhe.demo.api;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.enums.ImageFormat;
import com.arcsoft.face.toolkit.ImageInfo;
import com.juhe.demo.transfer.SimilarCompareBo;
import com.juhe.demo.util.Base64Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @CLassName ArcFaceAnalysis
 * @Description 虹软人脸识别工具类
 * @Author kuai.zhang
 * @Date 2019/8/29 14:02
 * @Version 1.0
 **/
@Slf4j
@Data
public class ArcFaceAnalysis {

    //相似度
    private float similarValue;

    //引擎
    private FaceEngine faceEngine;

    public ArcFaceAnalysis() {

    }

    public ArcFaceAnalysis(String appId, String sdkKey, String lib, float similarValue) {
        this.similarValue = similarValue;
        faceEngine = new FaceEngine(lib);
        //验证激活文件合法性
        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        int activeFileCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (activeFileCode != ErrorInfo.MOK.getValue()) {
            log.info("引擎激活文件不存在或不合法,尝试激活...");
            //激活引擎
            int activeCode = faceEngine.activeOnline(appId, sdkKey);
            if (activeCode != ErrorInfo.MOK.getValue() && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED
                .getValue()) {
                log.error("引擎激活失败");
            }
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);

        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        int initCode = faceEngine.init(engineConfiguration);
        if (initCode != ErrorInfo.MOK.getValue()) {
            log.info("初始化引擎失败");
        } else {
            log.info("初始化引擎成功");
        }
    }

    /**
     * @return byte[]
     * @Author kuai.zhang
     * @Description 文件特征提取
     * @Date 14:49 2019/8/29
     * @Param [file]
     **/
    public byte[] getFileFaceFeature(File file) {
        if (!file.exists()) {
            log.info("image file not exist");
            return null;
        }
        ImageInfo imageInfo = getRGBData(file);
        return faceFeature(imageInfo);
    }

    /**
     * @return byte[]
     * @Author kuai.zhang
     * @Description base64字符串特征提取
     * @Date 14:49 2019/8/29
     * @Param base64Content
     **/
    public byte[] getEncodeStrFaceFeature(String base64Content) {
        if (StringUtils.isBlank(base64Content)) {
            log.info("base64 content null");
            return null;
        }
        byte[] bytes = Base64Util.decode(base64Content);
        ImageInfo imageInfo = getRGBData(new ByteArrayInputStream(bytes));
        return faceFeature(imageInfo);
    }

    /**
     * @return SimilarCompareBo
     * @Author kuai.zhang
     * @Description 相似比对
     * @Date 14:50 2019/8/29
     * @Param [source, target, personalId]
     **/
    public SimilarCompareBo similarCompare(byte[] source, byte[] target, Long personalId) {
        if (source == null || target == null) {
            return null;
        }
        SimilarCompareBo bo = null;
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(target);
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(source);
        FaceSimilar faceSimilar = new FaceSimilar();
        int compareCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        if (compareCode == ErrorInfo.MOK.getValue()) {
            if (faceSimilar != null && faceSimilar.getScore() >= similarValue) {
                bo = new SimilarCompareBo(personalId, faceSimilar.getScore());
            }
        } else {
            log.info("ArcFace相似度比较失败,错误码为: " + compareCode);
        }
        return bo;
    }

    /**
     * @return void
     * @Author kuai.zhang
     * @Description 引擎卸载
     * @Date 14:50 2019/8/29
     * @Param []
     **/
    public void unInit() {
        if (faceEngine != null) {
            faceEngine.unInit();
        }
    }

    private byte[] faceFeature(ImageInfo imageInfo) {
        if (imageInfo == null) {
            return null;
        }
        byte[] rst = null;
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int detectCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(),
            ImageFormat.CP_PAF_BGR24, faceInfoList);
        if (detectCode == ErrorInfo.MOK.getValue() && !faceInfoList.isEmpty()) {
            FaceFeature faceFeature = new FaceFeature();
            int extractCode = faceEngine
                .extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(),
                    ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
            if (extractCode == ErrorInfo.MOK.getValue()) {
                rst = faceFeature.getFeatureData();
            } else {
                log.info("ArcFace提取特征值失败,错误码为: " + extractCode);
            }
        } else {
            log.info("ArcFace检测失败,错误码为: " + detectCode);
        }
        return rst;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        ArcFaceAnalysis arcFaceAnalysis = new ArcFaceAnalysis("GBqM7L1tcg6X5fvZmJm6cMjm5uEJk3ZEb9z8cvPFQG1e",
            "DGQmFvoP6SqG96mEY6qzAPZHFWZRmCmN62tRzjahq6JG",
            "D:/dll", 0.7f);
        //1573457588831
        byte[] b1 = arcFaceAnalysis.getFileFaceFeature(new File("D:/magic/1573457588310.jpg"));
        byte[] b2 = arcFaceAnalysis.getFileFaceFeature(new File("D:/magic/1573457588831.jpg"));
        log.info("b1: " + b1.length);
        log.info("b2: " + new String(b2));
        arcFaceAnalysis.similarCompare(b1, b2);
    }

    private void similarCompare(byte[] source, byte[] target) {
        if (source == null || target == null) {
            return;
        }
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(target);
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(source);
        FaceSimilar faceSimilar = new FaceSimilar();
        int compareCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        if (compareCode == ErrorInfo.MOK.getValue()) {
            log.info("相似度为: " + faceSimilar.getScore());
        } else {
            log.info("ArcFace相似度比较失败,错误码为: " + compareCode);
        }
    }
}
