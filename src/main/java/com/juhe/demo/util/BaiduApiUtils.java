package com.juhe.demo.util;

import com.juhe.demo.api.AipBodyAnalysis;
import com.juhe.demo.constant.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @CLassName BaiduApiUtils
 * @Description 百度API 工具类
 * @Author xxmfypp
 * @Date 2019/7/5 14:32
 * @Version 1.0
 **/
@Slf4j
public class BaiduApiUtils {


    /**
     * @return boolean
     * @Author xxmfypp
     * @Description 检测百度API是否存在组名，不存在则自行创建
     * @Date 14:35 2019/7/5
     * @Param [aipBodyAnalysis 百度API操作类, groupName 组名]
     **/
    public static boolean checkAndCreateGroup(AipBodyAnalysis aipBodyAnalysis, String groupName) {
        boolean exist = false;
        JSONObject jsonObject = aipBodyAnalysis.getGroupList(null);
        log.info("用户组列表：" + jsonObject.toString());
        String errorMsg = jsonObject.optString("error_msg", "FAILED");
        if (SystemConstant.BackResult.SUCCESS.equals(errorMsg)) {
            JSONArray jsonArray = jsonObject.optJSONObject("result").optJSONArray("group_id_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getString(i).equals(groupName)) {
                    return true;
                }
            }
            jsonObject = aipBodyAnalysis.groupAdd(groupName, null);
            errorMsg = jsonObject.optString("error_msg", "FAILED");
            if (SystemConstant.BackResult.SUCCESS.equals(errorMsg)) {
                exist = true;
            }
        }
        return exist;
    }

    /***
      * @Author xxmfypp
      * @Description //TODO
      * @Date 14:05 2019/8/30
      * @Param
      * @return
      **/
      //This is test
}
