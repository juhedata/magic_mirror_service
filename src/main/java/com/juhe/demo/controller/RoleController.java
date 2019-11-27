package com.juhe.demo.controller;

import com.juhe.demo.ao.RoleAO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant.Common;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import com.juhe.demo.service.IRoleService;
import com.juhe.demo.vo.PermissionVO;
import com.juhe.demo.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/")
@Api(value = "角色相关API", tags = "角色相关API")
@Validated
public class RoleController {

    @Autowired
    private IRoleService iRoleService;

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 添加角色
     * @Date 14:12 2019/7/18
     * @Param [roleAO]
     **/
    @ApiOperation("添加角色")
    @PostMapping(value = "role")
    @PreAuthorize("hasAuthority('role')")
    public CommonResult create(@Validated @RequestBody RoleAO roleAO, BindingResult result) {
        iRoleService.save(roleAO);
        return CommonResult.successPost(null);
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 修改角色
     * @Date 14:11 2019/7/18
     * @Param [id, roleAO]
     **/
    @ApiOperation("修改角色")
    @PutMapping(value = "role/{id}")
    @PreAuthorize("hasAuthority('role')")
    public CommonResult update(@NotNull(message = "角色编号不能为空") @PathVariable(value = "id") Long id,
        @Validated @RequestBody RoleAO roleAO, BindingResult result) {
        roleAO.setId(id);
        iRoleService.update(roleAO);
        return CommonResult.success();
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 批量删除角色
     * @Date 14:11 2019/7/18
     * @Param [ids]
     **/
    @PreAuthorize("hasAuthority('role')")
    @ApiOperation("批量删除角色")
    @DeleteMapping(value = "roles")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ids", value = "角色编号,多个以','号分割", required = true, dataType = "String", paramType = "query")
    })
    public CommonResult delete(@NotNull(message = "角色编号不能为空") @RequestParam(value = "ids") String idStr) {
        List<Long> ids = Arrays.stream(idStr.split(Common.DELIMITER)).map(id -> Long.valueOf(id))
            .collect(Collectors.toList());
        boolean success = iRoleService.removeByIds(ids);
        if (success) {
            return CommonResult.successDelete("批量删除角色成功");
        }
        return CommonResult.failed("批量删除角色失败");
    }

    /**
     * @return com.juhe.demo.common.CommonResult<java.util.List < com.juhe.demo.entity.Permission>>
     * @Author xxmfypp
     * @Description 获取相应角色权限
     * @Date 14:11 2019/7/18
     * @Param [roleId]
     **/
    @PreAuthorize("hasAuthority('role')")
    @ApiOperation("获取相应角色权限")
    @GetMapping(value = "role/permissions/{id}")
    public CommonResult<List<PermissionVO>> getPermissionList(
        @NotNull(message = "角色编号不能为空") @PathVariable(value = "id") Long roleId) {
        List<Permission> permissionList = iRoleService.getPermissionList(roleId);
        List<PermissionVO> vos = permissionList.stream().map(val -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(val, vo);
            return vo;
        }).collect(Collectors.toList());
        return CommonResult.success(vos);
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author xxmfypp
     * @Description 修改角色权限
     * @Date 14:11 2019/7/18
     * @Param [roleId, permissionIds]
     **/
    @PreAuthorize("hasAuthority('role')")
    @ApiOperation("修改角色权限")
    @PatchMapping(value = "role/permissions")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "role_id", value = "角色编号", dataType = "Long", required = true, paramType = "query"),
        @ApiImplicitParam(name = "permission_ids", value = "权限编号", dataType = "String", required = true, paramType = "query")
    })
    public CommonResult updatePermission(
        @NotNull(message = "角色编号不能为空") @RequestParam(value = "role_id") Long roleId,
        @NotBlank(message = "权限编号不能为空") @RequestParam(value = "permission_ids") String permissionStr) {
        List<Long> permissionIds = Arrays.stream(permissionStr.split(Common.DELIMITER))
            .map(permission -> Long.valueOf(permission))
            .collect(Collectors.toList());
        iRoleService.updatePermission(roleId, permissionIds);
        return CommonResult.success("修改角色权限成功");
    }

    /**
     * @return List
     * @Author xxmfypp
     * @Description 获取所有角色
     * @Date 14:10 2019/7/18
     * @Param []
     **/
    @PreAuthorize("hasAuthority('role')")
    @ApiOperation("获取所有角色")
    @GetMapping(value = "roles")
    public CommonResult<List<RoleVO>> list() {
        List<RoleVO> roleList = iRoleService.getRoleList();
        return CommonResult.success(roleList);
    }

    /**
     * @return com.juhe.demo.common.CommonResult<com.juhe.demo.entity.Role>
     * @Author kuai.zhang
     * @Description 获取单个角色
     * @Date 15:30 2019/8/1
     * @Param [roleId]
     **/
    @PreAuthorize("hasAuthority('role')")
    @ApiOperation("获取单个角色")
    @GetMapping(value = "role/{id}")
    public CommonResult<RoleVO> getRole(
        @NotNull(message = "角色id不能为空") @PathVariable(value = "id") Long roleId) {
        RoleVO roleVO = iRoleService.getRoleInfo(roleId);
        return CommonResult.success(roleVO);
    }

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author kuai.zhang
     * @Description 修改角色状态是否可用
     * @Date 15:38 2019/8/1
     * @Param [roleId, status]
     **/
    @PreAuthorize("hasAuthority('role')")
    @ApiOperation("修改角色状态是否可用")
    @PatchMapping(value = "role/status/{id}")
    public CommonResult updateRoleStatus(
        @NotNull(message = "角色id不能为空") @PathVariable(value = "id") Long roleId,
        @NotNull(message = "角色状态不能为空") @RequestParam(value = "status") Integer status) {
        if (!(0 == status || 1 == status)) {
            return CommonResult.failed("角色状态不合法");
        }
        Role role = iRoleService.getById(roleId);
        if (role == null) {
            return CommonResult.failed("角色不存在");
        }
        role.setStatus(status);
        iRoleService.updateById(role);
        return CommonResult.success("角色状态修改成功");
    }

}
