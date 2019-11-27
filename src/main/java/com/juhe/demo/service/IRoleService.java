package com.juhe.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juhe.demo.ao.RoleAO;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import com.juhe.demo.vo.RoleVO;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface IRoleService extends IService<Role> {

    List<Permission> getPermissionList(Long roleId);

    void updatePermission(Long roleId, List<Long> permissionIds);

    void save(RoleAO roleAO);

    void update(RoleAO roleAO);

    RoleVO getRoleInfo(Long roleId);

    List<RoleVO> getRoleList();

}
