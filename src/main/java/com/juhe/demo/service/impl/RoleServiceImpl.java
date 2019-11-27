package com.juhe.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juhe.demo.ApplicationException;
import com.juhe.demo.ao.RoleAO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.common.ResultCodeEnum;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import com.juhe.demo.entity.RolePermissionRelation;
import com.juhe.demo.mapper.PermissionMapper;
import com.juhe.demo.mapper.RoleMapper;
import com.juhe.demo.mapper.RolePermissionRelationMapper;
import com.juhe.demo.service.IRoleService;
import com.juhe.demo.vo.RoleVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {


    @Resource
    RoleMapper roleMapper;

    @Resource
    RolePermissionRelationMapper rolePermissionRelationMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> getPermissionList(Long roleId) {
        return roleMapper.getPermissionList(roleId);
    }

    @Override
    public void updatePermission(Long roleId, List<Long> permissionIds) {

        QueryWrapper<RolePermissionRelation> rolePermissionWrapper = new QueryWrapper<>();
        rolePermissionWrapper.lambda().eq(RolePermissionRelation::getRoleId, roleId);
        //获取当前角色所有的权限
        List<RolePermissionRelation> rolePermissionRelationList = rolePermissionRelationMapper
            .selectList(rolePermissionWrapper);
        List<Long> oldPermissionids = new ArrayList<>();
        if (rolePermissionRelationList != null && !rolePermissionRelationList.isEmpty()) {
            for (RolePermissionRelation rolePermissionRelation : rolePermissionRelationList) {
                oldPermissionids.add(rolePermissionRelation.getPermissionId());
            }
        }

        //需要插入的权限编号
        List<Long> needAddPermissionids = new ArrayList<>();

        for (Long id : permissionIds) {
            if (!oldPermissionids.contains(id)) {
                needAddPermissionids.add(id);
            } else {
                //删除已存在的权限编号,剩余需要删除的权限编号
                oldPermissionids.remove(id);
            }
        }
        if (oldPermissionids != null && !oldPermissionids.isEmpty()) {
            for (Long id : oldPermissionids) {
                UpdateWrapper<RolePermissionRelation> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().eq(RolePermissionRelation::getRoleId, roleId)
                    .eq(RolePermissionRelation::getPermissionId, id);
                rolePermissionRelationMapper.delete(updateWrapper);
            }
        }
        if (needAddPermissionids != null && !needAddPermissionids.isEmpty()) {
            for (Long id : needAddPermissionids) {
                RolePermissionRelation rolePermissionRelation = new RolePermissionRelation();
                rolePermissionRelation.setPermissionId(id);
                rolePermissionRelation.setRoleId(roleId);
                rolePermissionRelationMapper.insert(rolePermissionRelation);
            }
        }
    }

    @Override
    public void save(RoleAO roleAO) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getCode, roleAO.getCode());
        queryWrapper.or().eq(Role::getName, roleAO.getName());
        int existCount = count(queryWrapper);
        if (existCount > 0) {
            throw new ApplicationException("用户名或者编码已存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleAO, role, "id");
        role.setCreateTime(LocalDateTime.now());
        this.save(role);
        saveRolePermissionRelations(role.getId(), roleAO.getPermissionId());
    }

    @Override
    public void update(RoleAO roleAO) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Role::getId, roleAO.getId());
        queryWrapper.and(query -> query.eq(Role::getCode, roleAO.getCode()).or().eq(Role::getName, roleAO.getName()));
        int existCount = count(queryWrapper);
        if (existCount > 0) {
            throw new ApplicationException("用户名或者编码已存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleAO, role);
        this.saveOrUpdate(role);

        LambdaQueryWrapper<RolePermissionRelation> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.eq(RolePermissionRelation::getRoleId, role.getId());
        rolePermissionRelationMapper.delete(rolePermissionWrapper);
        saveRolePermissionRelations(role.getId(), roleAO.getPermissionId());
    }

    @Override
    public RoleVO getRoleInfo(Long roleId) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new ApplicationException("角色未找到");
        }
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        getRolePermissionList(roleVO);
        return roleVO;
    }

    @Override
    public List<RoleVO> getRoleList() {
        List<Role> roles = list();
        List<RoleVO> vos = roles.stream().map(
            val -> {
                RoleVO vo = new RoleVO();
                BeanUtils.copyProperties(val, vo);
                getRolePermissionList(vo);
                return vo;
            }).collect(Collectors.toList());
        return vos;
    }

    private void saveRolePermissionRelations(Long roleId, Long[] permissionIds) {
        if (roleId == null || permissionIds == null || permissionIds.length == 0) {
            return;
        }
        RolePermissionRelation rolePermissionRelation;
        for (Long permissionId : permissionIds) {
            rolePermissionRelation = new RolePermissionRelation();
            rolePermissionRelation.setRoleId(roleId);
            rolePermissionRelation.setPermissionId(permissionId);
            rolePermissionRelationMapper.insert(rolePermissionRelation);
        }
    }

    private void getRolePermissionList(RoleVO roleVO) {
        LambdaQueryWrapper<RolePermissionRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermissionRelation::getRoleId, roleVO.getId());
        List<RolePermissionRelation> rolePermissions = rolePermissionRelationMapper.selectList(queryWrapper);
        Long[] permissionId = new Long[rolePermissions.size()];
        String[] permissionName = new String[rolePermissions.size()];
        Permission permission;
        for (int index = 0; index < rolePermissions.size(); index++) {
            permissionId[index] = rolePermissions.get(index).getPermissionId();
            permission = permissionMapper.selectById(rolePermissions.get(index).getPermissionId());
            if (permission != null) {
                permissionName[index] = permission.getName();
            }
        }
        roleVO.setPermissionId(permissionId);
        roleVO.setPermissionName(permissionName);
    }
}
