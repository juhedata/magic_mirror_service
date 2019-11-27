package com.juhe.demo.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.entity.GroupName;
import com.juhe.demo.entity.PersonalInfo;
import com.juhe.demo.service.IGroupNameService;
import com.juhe.demo.service.IPersonalInfoService;
import com.juhe.demo.vo.GroupNameVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 组名称 前端控制器
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Api(value = "组名称相关API", tags = "组名称相关API")
@RestController
@RequestMapping("/")
@Validated
public class GroupNameController {

    @Autowired
    private IGroupNameService iGroupNameService;

    @Autowired
    private IPersonalInfoService iPersonalInfoService;

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author kuai.zhang
     * @Description 显示分组信息
     * @Date 11:11 2019/7/18
     * @Param [name]
     **/
    @ApiOperation(value = "显示分组信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "分组名称", dataType = "String", paramType = "query")
    })
    @GetMapping(value = "groups")
    public CommonResult<List<GroupNameVO>> listGroupName(@RequestParam(value = "name", required = false) String name) {
        List<GroupNameVO> groupNames = iGroupNameService.listGroupName(name);
        return CommonResult.success(groupNames);
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 新增分组信息
     * @Date 11:11 2019/7/12
     * @Param [name]
     **/
    @ApiOperation(value = "保存分组信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "分组名称", required = true, dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "group")
    public CommonResult saveGroupName(
        @NotBlank(message = "分组名称不能为空") @RequestParam(value = "name") String name) {
        boolean result = iGroupNameService.saveGroupName(name);
        if (result) {
            return CommonResult.successPost("保存分组信息成功");
        } else {
            return CommonResult.failed("分组名称已经存在");
        }
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 删除分组信息
     * @Date 11:12 2019/7/12
     * @Param [id]
     **/
    @ApiOperation(value = "删除分组信息")
    @DeleteMapping(value = "group/{id}")
    public CommonResult deleteGroupName(
        @NotNull(message = "分组编号不能为空") @PathVariable Long id) {
        LambdaUpdateWrapper<PersonalInfo> exWrapper = new LambdaUpdateWrapper<PersonalInfo>()
            .eq(PersonalInfo::getGroupId, id);
        if (iPersonalInfoService.count(exWrapper) > 0) {
            return CommonResult.failed("请先将组内人员移除,才能进行删除操作");
        }
        if (iGroupNameService.removeById(id)) {
            iPersonalInfoService.list(exWrapper).stream()
                .forEach(personalInfo -> iPersonalInfoService.removePersonalImageAndMacs(personalInfo.getId()));
            iPersonalInfoService.update(
                exWrapper.set(PersonalInfo::getDeleted, 1));
            return CommonResult.successDelete("删除成功");
        } else {
            return CommonResult.failed("删除失败");
        }
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 修改分组信息
     * @Date 11:12 2019/7/12
     * @Param [id, name]
     **/
    @ApiOperation(value = "修改分组信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "分组编号", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "name", value = "分组名称", required = true, dataType = "String", paramType = "query")
    })
    @PatchMapping(value = "group/{id}")
    public CommonResult updateGroupName(
        @NotNull(message = "分组编号不能为空") @PathVariable Long id,
        @NotBlank(message = "分组名称不能为空") @RequestParam(value = "name") String name) {
        GroupName groupName = iGroupNameService.getById(id);
        if (groupName == null) {
            return CommonResult.failed("编号对应的分组信息不存在");
        }
        int count = iGroupNameService.updateExistGroupName(id, name);
        if (count > 0) {
            return CommonResult.failed("分组名称已经存在");
        }
        groupName.setName(name);
        if (iGroupNameService.updateById(groupName)) {
            return CommonResult.success("修改成功");
        } else {
            return CommonResult.failed("修改失败");
        }
    }
}
