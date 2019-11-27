package com.juhe.demo.controller;


import com.juhe.demo.ao.AdminAO;
import com.juhe.demo.ao.LoginAO;
import com.juhe.demo.ao.PasswordAO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant.Common;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import com.juhe.demo.service.IAdminService;
import com.juhe.demo.service.IPermissionService;
import com.juhe.demo.vo.AdminVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * 后台用户表 前端控制器
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/")
@Api(value = "管理员相关API", tags = "管理员相关API")
@Slf4j
@Validated
public class AdminController {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private IAdminService iAdminService;

    @Autowired
    private IPermissionService iPermissionService;

    @ApiOperation(value = "登录以后返回token")
    @PostMapping(value = "/login")
    public CommonResult login(@Validated @RequestBody LoginAO loginAO, BindingResult result) {
        String token = iAdminService.login(loginAO.getUsername(), loginAO.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.successPost(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @GetMapping(value = "/token")
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = iAdminService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed();
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "登出功能")
    @PostMapping(value = "/logout")
    public CommonResult logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            Optional<Cookie> optional = Arrays.stream(request.getCookies())
                .filter(val -> tokenHeader.equals(val.getName()))
                .findFirst();
            if (optional.isPresent()) {
                Cookie cookie = new Cookie(tokenHeader, null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        return CommonResult.successPost(null);
    }

    @ApiOperation("获取所有管理员信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "用户名", dataType = "String", paramType = "query")
    })
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping(value = "/admins")
    public CommonResult<List<AdminVO>> getAllAdmins(@RequestParam(value = "name", required = false) String name) {
        List<AdminVO> adminList = iAdminService.getAdminList(name);
        return CommonResult.success(adminList);
    }

    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation("获取指定管理员信息")
    @GetMapping(value = "/admin/{id}")
    public CommonResult<AdminVO> getItem(@NotNull(message = "管理员编号不能为空") @PathVariable Long id) {
        AdminVO adminVO = iAdminService.getAdminById(id);
        return CommonResult.success(adminVO);
    }

    //@PreAuthorize("hasAuthority('admin')")
    @ApiOperation("获取当前管理员信息")
    @GetMapping(value = "/admin/current")
    public CommonResult<AdminVO> getCurrentAdmin(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        if (StringUtils.isEmpty(token)) {
            return CommonResult.failed("头信息没有token");
        }
        AdminVO adminVO = iAdminService.getAdminInfoByToken(token.substring(tokenHead.length()));
        return CommonResult.success(adminVO);
    }

    @ApiOperation("新增用户信息")
    @PostMapping(value = "/admin")
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult save(@Validated @RequestBody AdminAO adminAO, BindingResult result) {
        boolean success = iAdminService.saveAdmin(adminAO);
        if (success) {
            return CommonResult.successPost("新增用户信息成功");
        }
        return CommonResult.failed("新增用户信息失败,用户名重复");
    }

    @ApiOperation("修改指定用户信息")
    @PatchMapping(value = "/admin/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult update(@PathVariable(value = "id") Long id,
        @Validated @RequestBody AdminAO adminAO, BindingResult result) {
        if (adminAO.getId() == null) {
            adminAO.setId(id);
        }
        boolean success = iAdminService.updateAdmin(adminAO);
        if (success) {
            return CommonResult.success("修改用户信息成功");
        }
        return CommonResult.failed("修改用户信息失败");
    }

    @ApiOperation("删除指定用户信息")
    @DeleteMapping(value = "/admin/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult delete(@NotNull(message = "管理员编号不能为空") @PathVariable Long id) {
        boolean success = iAdminService.deleteAdminById(id);
        if (success) {
            return CommonResult.successDelete("删除用户信息成功");
        }
        return CommonResult.failed("删除用户信息失败");
    }

    @ApiOperation("给用户分配角色")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "admin_id", value = "管理员编号", required = true, dataType = "Long", paramType = "query"),
        @ApiImplicitParam(name = "role_ids", value = "角色编号，多个用','号隔开", required = true, dataType = "String", paramType = "query")
    })
    @PutMapping(value = "/admin/roles")
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult updateRole(
        @NotNull(message = "管理员编号不能为空") @RequestParam(value = "admin_id") Long adminId,
        @NotNull(message = "角色编号不能为空") @RequestParam(value = "role_ids") String roleIdStr) {
        String[] roleIdArr = roleIdStr.split(Common.DELIMITER);
        List<Long> roleIds = Arrays.stream(roleIdArr).map(roleId -> Long.valueOf(roleId))
            .collect(Collectors.toList());
        iAdminService.updateRole(adminId, roleIds);
        return CommonResult.success("分配角色成功");
    }

    @ApiOperation("获取指定用户的角色")
    @GetMapping(value = "/admin/roles/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult<List<Role>> getRoleList(@NotNull(message = "管理员编号不能为空") @PathVariable Long id) {
        List<Role> roleList = iAdminService.getRoleList(id);
        return CommonResult.success(roleList);
    }

    @ApiOperation("获取用户所有权限（包括+-权限）")
    @GetMapping(value = "/admin/permissions/{id}")
    //@PreAuthorize("hasAuthority('admin')")
    public CommonResult<List<Permission>> getPermissionList(
        @NotNull(message = "管理员编号不能为空") @PathVariable Long id) {
        List<Permission> permissionList = iPermissionService.getUserAuthorityList(id);
        return CommonResult.success(permissionList);
    }

    @ApiOperation("修改用户密码")
    @PostMapping("/admin/password/{id}")
    //@PreAuthorize("hasAuthority('admin')")
    public CommonResult updatePassword(@PathVariable(value = "id") Long id,
        @Validated @RequestBody PasswordAO passwordAO) {
        if (!passwordAO.getNewPassword().equals(passwordAO.getConfirmPassword())) {
            return CommonResult.validateFailed("新密码和确认密码不一致");
        }
        iAdminService.updatePassword(id, passwordAO);
        return CommonResult.successPost(null);
    }

}
