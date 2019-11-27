package com.juhe.demo.api;

import com.baidu.aip.client.BaseClient;
import com.baidu.aip.error.AipError;
import com.baidu.aip.face.FaceVerifyRequest;
import com.baidu.aip.http.AipRequest;
import com.baidu.aip.http.EBodyFormat;
import com.baidu.aip.util.Base64Util;
import com.baidu.aip.util.Util;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
public class AipBodyAnalysis extends BaseClient {

    static final String BODY_ATTR = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_attr";
    static final String BODY_NUM = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_num";
    static final String USER_ADD = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
    static final String USER_DELETE = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete";

    static final String GROUP_ADD = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/add";
    static final String GROUP_DELETE = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/delete";
    static final String GROUP_GETLIST = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getlist";


    static final String MULTI_SEARCH = "https://aip.baidubce.com/rest/2.0/face/v3/multi-search";

    static final String DETECT = "https://aip.baidubce.com/rest/2.0/face/v3/detect";

    static final String FACE_VERIFY = "https://aip.baidubce.com/rest/2.0/face/v3/faceverify";

    public AipBodyAnalysis(String appId, String apiKey, String secretKey) {
        super(appId, apiKey, secretKey);
    }

    /**
     * 人体检测与属性识别接口 对于输入的一张图片（可正常解码，且长宽比适宜），**检测图像中的所有人体并返回每个人体的矩形框位置，识别人体的静态属性和行为，共支持20余种属性，包括：性别、年龄阶段、衣着（含类别/颜色）、是否戴帽子、是否戴眼镜、是否背包、是否使用手机、身体朝向等**。
     *
     * @param image - 二进制图像数据
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: type gender,<br>age,<br>lower_wear,<br>upper_wear,<br>headwear,<br>glasses,<br>upper_color,<br>lower_color,<br>cellphone,<br>upper_wear_fg,<br>upper_wear_texture,<br>lower_wear_texture,<br>orientation,<br>umbrella,<br>bag,<br>smoke,<br>vehicle,<br>carrying_item,<br>upper_cut,<br>lower_cut,<br>occlusion
     * &#124; 1）可选值说明：<br>gender-性别，<br>age-年龄阶段，<br>lower_wear-下身服饰，<br>upper_wear-上身服饰，<br>headwear-是否戴帽子，<br>glasses-是否戴眼镜，<br>upper_color-上身服饰颜色，<br>lower_color-下身服饰颜色，<br>cellphone-是否使用手机，<br>upper_wear_fg-上身服饰细分类，<br>upper_wear_texture-上身服饰纹理，<br>orientation-身体朝向，<br>umbrella-是否撑伞；<br>bag-背包,<br>smoke-是否吸烟,<br>vehicle-交通工具,<br>carrying_item-是否有手提物,<br>upper_cut-上方截断,<br>lower_cut-下方截断,<br>occlusion-遮挡<br>2）type
     * 参数值可以是可选值的组合，用逗号分隔；**如果无此参数默认输出全部20个属性**
     * @return JSONObject
     */
    public JSONObject bodyAttr(byte[] image, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        String base64Content = Base64Util.encode(image);

        request.addBody("image", base64Content);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(BODY_ATTR);
        postOperation(request);
        return requestServer(request);
    }

    /**
     * 人体检测与属性识别接口 对于输入的一张图片（可正常解码，且长宽比适宜），**检测图像中的所有人体并返回每个人体的矩形框位置，识别人体的静态属性和行为，共支持20余种属性，包括：性别、年龄阶段、衣着（含类别/颜色）、是否戴帽子、是否戴眼镜、是否背包、是否使用手机、身体朝向等**。
     *
     * @param image - 本地图片路径
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: type gender,<br>age,<br>lower_wear,<br>upper_wear,<br>headwear,<br>glasses,<br>upper_color,<br>lower_color,<br>cellphone,<br>upper_wear_fg,<br>upper_wear_texture,<br>lower_wear_texture,<br>orientation,<br>umbrella,<br>bag,<br>smoke,<br>vehicle,<br>carrying_item,<br>upper_cut,<br>lower_cut,<br>occlusion
     * &#124; 1）可选值说明：<br>gender-性别，<br>age-年龄阶段，<br>lower_wear-下身服饰，<br>upper_wear-上身服饰，<br>headwear-是否戴帽子，<br>glasses-是否戴眼镜，<br>upper_color-上身服饰颜色，<br>lower_color-下身服饰颜色，<br>cellphone-是否使用手机，<br>upper_wear_fg-上身服饰细分类，<br>upper_wear_texture-上身服饰纹理，<br>orientation-身体朝向，<br>umbrella-是否撑伞；<br>bag-背包,<br>smoke-是否吸烟,<br>vehicle-交通工具,<br>carrying_item-是否有手提物,<br>upper_cut-上方截断,<br>lower_cut-下方截断,<br>occlusion-遮挡<br>2）type
     * 参数值可以是可选值的组合，用逗号分隔；**如果无此参数默认输出全部20个属性**
     * @return JSONObject
     */
    public JSONObject bodyAttr(String image, HashMap<String, String> options) {
        try {
            byte[] data = Util.readFileByBytes(image);
            return bodyAttr(data, options);
        } catch (IOException e) {
            e.printStackTrace();
            return AipError.IMAGE_READ_ERROR.toJsonResult();
        }
    }

    /**
     * 人流量统计接口 对于输入的一张图片（可正常解码，且长宽比适宜），**识别和统计图像当中的人体个数（静态统计，暂不支持追踪和去重）**。
     *
     * @param image - 二进制图像数据
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: area 特定框选区域坐标，逗号分隔，如‘x1,y1,x2,y2,x3,y3...xn,yn'，默认尾点和首点相连做闭合，**此参数为空或无此参数默认识别整个图片的人数**
     * show 是否输出渲染的图片，默认不返回，**选true时返回渲染后的图片(base64)**，其它无效值或为空则默认false
     * @return JSONObject
     */
    public JSONObject bodyNum(byte[] image, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        String base64Content = Base64Util.encode(image);
        request.addBody("image", base64Content);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(BODY_NUM);
        postOperation(request);
        return requestServer(request);
    }

    /**
     * 人流量统计接口 对于输入的一张图片（可正常解码，且长宽比适宜），**识别和统计图像当中的人体个数（静态统计，暂不支持追踪和去重）**。
     *
     * @param image - 本地图片路径
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: area 特定框选区域坐标，逗号分隔，如‘x1,y1,x2,y2,x3,y3...xn,yn'，默认尾点和首点相连做闭合，**此参数为空或无此参数默认识别整个图片的人数**
     * show 是否输出渲染的图片，默认不返回，**选true时返回渲染后的图片(base64)**，其它无效值或为空则默认false
     * @return JSONObject
     */
    public JSONObject bodyNum(String image, HashMap<String, String> options) {
        try {
            byte[] data = Util.readFileByBytes(image);
            return bodyNum(data, options);
        } catch (IOException e) {
            e.printStackTrace();
            return AipError.IMAGE_READ_ERROR.toJsonResult();
        }
    }


    /**
     * 人脸注册接口
     *
     * @param image - 图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断。注：组内每个uid下的人脸图片数目上限为20张
     * @param imageType - 图片类型 **BASE64**:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；**URL**:图片的 URL地址(
     * 可能由于网络等原因导致下载图片时间过长)**；FACE_TOKEN**: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     * @param groupId - 用户组id（由数字、字母、下划线组成），长度限制128B
     * @param userId - 用户id（由数字、字母、下划线组成），长度限制128B
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: user_info 用户资料，长度限制256B quality_control 图片质量控制
     * **NONE**: 不进行控制 **LOW**:较低的质量要求 **NORMAL**: 一般的质量要求 **HIGH**: 较高的质量要求 **默认 NONE** liveness_control 活体检测控制
     * **NONE**: 不进行控制 **LOW**:较低的活体要求(高通过率 低攻击拒绝率) **NORMAL**: 一般的活体要求(平衡的攻击拒绝率, 通过率) **HIGH**: 较高的活体要求(高攻击拒绝率 低通过率)
     * **默认NONE** action_type 操作方式  APPEND: 当user_id在库中已经存在时，对此user_id重复注册时，新注册的图片默认会追加到该user_id下,REPLACE :
     * 当对此user_id重复注册时,则会用新图替换库中该user_id下所有图片,默认使用APPEND
     * @return JSONObject
     */
    public JSONObject addUser(String image, String imageType, String groupId, String userId,
        HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        request.addBody("image", image);

        request.addBody("image_type", imageType);

        request.addBody("group_id", groupId);

        request.addBody("user_id", userId);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(USER_ADD);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        postOperation(request);
        return requestServer(request);
    }


    /**
     * 删除用户接口
     *
     * @param groupId - 用户组id（由数字、字母、下划线组成），长度限制128B
     * @param userId - 用户id（由数字、字母、下划线组成），长度限制128B
     * @param options - 可选参数对象，key: value都为string类型 options - options列表:
     * @return JSONObject
     */
    public JSONObject deleteUser(String groupId, String userId, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        request.addBody("group_id", groupId);

        request.addBody("user_id", userId);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(USER_DELETE);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        postOperation(request);
        return requestServer(request);
    }


    /**
     * 人脸搜索 M:N 识别接口
     *
     * @param image - 图片信息(**总数据大小应小于10M**)，图片上传方式根据image_type来判断
     * @param imageType - 图片类型 **BASE64**:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；**URL**:图片的 URL地址(
     * 可能由于网络等原因导致下载图片时间过长)**；FACE_TOKEN**: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     * @param groupIdList - 从指定的group中进行查找 用逗号分隔，**上限20个**
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: max_face_num
     * 最多处理人脸的数目<br>**默认值为1(仅检测图片中面积最大的那个人脸)** **最大值10** match_threshold 匹配阈值（设置阈值后，score低于此阈值的用户信息将不会返回） 最大100 最小0
     * 默认80
     * <br>**此阈值设置得越高，检索速度将会越快，推荐使用默认阈值`80`** quality_control 图片质量控制  **NONE**: 不进行控制 **LOW**:较低的质量要求 **NORMAL**:
     * 一般的质量要求 **HIGH**: 较高的质量要求 **默认 NONE** liveness_control 活体检测控制  **NONE**: 不进行控制 **LOW**:较低的活体要求(高通过率 低攻击拒绝率)
     * **NORMAL**: 一般的活体要求(平衡的攻击拒绝率, 通过率) **HIGH**: 较高的活体要求(高攻击拒绝率 低通过率) **默认NONE** max_user_num
     * 查找后返回的用户数量。返回相似度最高的几个用户，默认为1，最多返回50个。
     * @return JSONObject
     */
    public JSONObject multiSearch(String image, String imageType, String groupIdList, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        request.addBody("image", image);

        request.addBody("image_type", imageType);

        request.addBody("group_id_list", groupIdList);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(MULTI_SEARCH);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        postOperation(request);
        return requestServer(request);
    }


    /**
     * 创建用户组接口
     *
     * @param groupId - 用户组id（由数字、字母、下划线组成），长度限制128B
     * @param options - 可选参数对象，key: value都为string类型 options - options列表:
     * @return JSONObject
     */
    public JSONObject groupAdd(String groupId, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        request.addBody("group_id", groupId);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(GROUP_ADD);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        postOperation(request);
        return requestServer(request);
    }

    /**
     * 删除用户组接口
     *
     * @param groupId - 用户组id（由数字、字母、下划线组成），长度限制128B
     * @param options - 可选参数对象，key: value都为string类型 options - options列表:
     * @return JSONObject
     */
    public JSONObject groupDelete(String groupId, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        request.addBody("group_id", groupId);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(GROUP_DELETE);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        postOperation(request);
        return requestServer(request);
    }

    /**
     * 组列表查询接口
     *
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: start 默认值0，起始序号 length 返回数量，默认值100，最大值1000
     * @return JSONObject
     */
    public JSONObject getGroupList(HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(GROUP_GETLIST);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        postOperation(request);
        return requestServer(request);
    }


    /**
     * 人脸检测接口
     *
     * @param image - 图片信息(**总数据大小应小于10M**)，图片上传方式根据image_type来判断
     * @param imageType - 图片类型 **BASE64**:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；**URL**:图片的 URL地址(
     * 可能由于网络等原因导致下载图片时间过长)**；FACE_TOKEN**: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     * @param options - 可选参数对象，key: value都为string类型 options - options列表: face_field
     * 包括**age,beauty,expression,face_shape,gender,glasses,landmark,landmark72，landmark150，race,quality,eye_status,emotion,face_type信息**
     * <br> 逗号分隔. 默认只返回face_token、人脸框、概率和旋转角度 max_face_num 最多处理人脸的数目，默认值为1，仅检测图片中面积最大的那个人脸；**最大值10**，检测图片中面积最大的几张人脸。
     * face_type 人脸的类型 **LIVE**表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等**IDCARD**表示身份证芯片照：二代身份证内置芯片中的人像照片
     * **WATERMARK**表示带水印证件照：一般为带水印的小图，如公安网小图 **CERT**表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片 默认**LIVE** liveness_control 活体检测控制
     * **NONE**: 不进行控制 **LOW**:较低的活体要求(高通过率 低攻击拒绝率) **NORMAL**: 一般的活体要求(平衡的攻击拒绝率, 通过率) **HIGH**: 较高的活体要求(高攻击拒绝率 低通过率)
     * **默认NONE**
     * @return JSONObject
     */
    public JSONObject detect(String image, String imageType, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        request.addBody("image", image);

        request.addBody("image_type", imageType);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(DETECT);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        postOperation(request);
        return requestServer(request);
    }

    /**
     * @return face_liveness 活体分数值,thresholds 由服务端返回最新的阈值数据（随着模型的优化，阈值可能会变化），可以作为活体判断的依据。 frr_1e-4：万分之一误识率的阈值；frr_1e-3：
     * 千分之一误识率的阈值；frr_1e-2：百分之一误识率的阈值。误识率越底，准确率越高。face_list 每张图片的详细信息描述，如果只上传一张图片，则只返回一个结果。
     * @Author kuai.zhang
     * @Description 在线活体检测
     * @Date 9:17 2019/9/5
     * @Param image 图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
     * @Param image_type 图片类型 BASE64:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；URL:图片的 URL地址(
     * 可能由于网络等原因导致下载图片时间过长)； FACE_TOKEN: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     * @Param face_fields 包括age,beauty,expression,faceshape,gender,glasses,landmark,race,quality,facetype信息，逗号分隔，默认只返回face_token、活体数、人脸框、概率和旋转角度。
     **/
    public synchronized JSONObject faceVerify(String image) {
        AipRequest request = new AipRequest();
        this.preOperation(request);
        JSONArray arr = new JSONArray();
        FaceVerifyRequest req = new FaceVerifyRequest(image, "BASE64");
        arr.put(req.toJsonObject());
        request.addBody("body", arr.toString());
        request.setBodyFormat(EBodyFormat.RAW_JSON_ARRAY);
        request.setUri(FACE_VERIFY);
        this.postOperation(request);
        return this.requestServer(request);
    }

}
