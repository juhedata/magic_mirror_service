package com.juhe.demo.util;

import com.juhe.demo.constant.SystemConstant.Personal;
import java.io.File;

/**
 * @CLassName FileUtil
 * @Description 文件操作工具类
 * @Author kuai.zhang
 * @Date 2019/7/18 16:20
 * @Version 1.0
 **/
public class FileUtil {

    /**
     * @return void
     * @Author kuai.zhang
     * @Description 删除图片
     * @Date 16:20 2019/7/18
     * @Param [savePath, imageName]
     **/
    public static void deleteImage(String savePath, String imageName) {
        File image = new File(savePath + File.separator + imageName);
        if (image.exists()) {
            image.delete();
        }
        image = new File(getCompressImageName(savePath, imageName));
        if (image.exists()) {
            image.delete();
        }
    }

    public static String getCompressImageName(String savePath, String imageName) {
        StringBuffer sb = new StringBuffer(savePath);
        sb.append(File.separator).append(imageName, 0, imageName.indexOf(".")).append(Personal.IMAGE_COMPRESS)
            .append(imageName.substring(imageName.indexOf(".")));
        return sb.toString();
    }

}