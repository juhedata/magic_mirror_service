package com.juhe.demo.controller;

import com.juhe.demo.ao.PermissionAO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant.Common;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.service.IPermissionService;
import com.juhe.demo.vo.PermissionNode;
import com.juhe.demo.vo.PermissionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 权限菜单表 前端控制器
 * </p>
 *
 * @author xuxm
 * @since 2019-07-18
 */
@Api(value = "权限相关API", tags = "权限相关API")
@RestController
@RequestMapping("/")
@Validated
public class PermissionController {

    @Autowired
    private IPermissionService iPermissionService;

    @ApiOperation("添加权限")
    @PostMapping(value = "permission")
    public CommonResult create(@Validated @RequestBody PermissionAO permissionAO, BindingResult bindingResult) {
        iPermissionService.save(permissionAO);
        return CommonResult.successPost("新增成功");
    }

    @ApiOperation("修改权限")
    @PutMapping(value = "permission/{id}")
    public CommonResult update(@NotNull(message = "权限编号不能为空") @PathVariable Long id,
        @Validated @RequestBody PermissionAO permissionAO, BindingResult bindingResult) {
        permissionAO.setId(id);
        iPermissionService.update(permissionAO);
        return CommonResult.success("修改成功");
    }

    @ApiOperation("根据id批量删除权限")
    @DeleteMapping(value = "permissions")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ids", value = "权限编号,多个用,号隔开", required = true, dataType = "String", paramType = "query")
    })
    public CommonResult delete(
        @NotNull(message = "权限编号不能为空") @RequestParam(value = "ids") String idStr) {
        List<Long> ids = Arrays.stream(idStr.split(Common.DELIMITER)).map(id -> Long.valueOf(id))
            .collect(Collectors.toList());
        iPermissionService.delete(ids);
        return CommonResult.successDelete("批量删除权限成功");
    }

    @ApiOperation("以层级结构返回所有权限")
    @GetMapping(value = "permission/treelist")
    public CommonResult<List<PermissionNode>> treeList() {
        List<PermissionNode> permissionNodeList = iPermissionService.treeList();
        return CommonResult.success(permissionNodeList);
    }

    @ApiOperation("获取所有权限列表")
    @GetMapping(value = "permissions")
    public CommonResult<List<PermissionVO>> list() {
        List<Permission> permissionList = iPermissionService.list();
        List<PermissionVO> vos = permissionList.stream().map(val -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(val, vo);
            return vo;
        }).collect(Collectors.toList());
        return CommonResult.success(vos);
    }

}
