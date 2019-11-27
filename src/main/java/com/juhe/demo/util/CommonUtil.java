package com.juhe.demo.util;

import com.juhe.demo.constant.SystemConstant.Common;
import java.util.Arrays;

/**
 * @CLassName CommonUtil
 * @Description 工具类
 * @Author kuai.zhang
 * @Date 2019/8/14 13:50
 * @Version 1.0
 **/
public class CommonUtil {

    public static boolean validPersonMac(String mac) {
        boolean valid = Arrays.stream(mac.split(Common.DELIMITER)).allMatch(val -> val.matches(Common.MAC_VALID));
        return valid;
    }

}