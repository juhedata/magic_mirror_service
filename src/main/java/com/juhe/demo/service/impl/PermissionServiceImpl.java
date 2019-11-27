package com.juhe.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juhe.demo.ApplicationException;
import com.juhe.demo.ao.PermissionAO;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.RolePermissionRelation;
import com.juhe.demo.mapper.PermissionMapper;
import com.juhe.demo.mapper.RolePermissionRelationMapper;
import com.juhe.demo.service.IAdminService;
import com.juhe.demo.service.IPermissionService;
import com.juhe.demo.vo.PermissionNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限菜单表 服务实现类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-18
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Resource
    RolePermissionRelationMapper rolePermissionRelationMapper;

    @Autowired
    private IAdminService iAdminService;

    @Override
    public void delete(List<Long> ids) {
        removeByIds(ids);

        for (Long id : ids) {
            QueryWrapper<RolePermissionRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(RolePermissionRelation::getPermissionId, id);
            rolePermissionRelationMapper.delete(queryWrapper);
        }
    }

    @Override
    public List<PermissionNode> treeList() {
        List<Permission> permissionList = list();
        List<PermissionNode> permissionNodeList = permissionList.stream()
            .filter(permission -> permission.getPid() == null || permission.getPid().equals(0L))
            .map(permission -> covert(permission, permissionList)).collect(Collectors.toList());
        return permissionNodeList;
    }

    @Override
    public List<PermissionNode> treeListByParentId(Long parentId) {
        List<Permission> permissions = this.list();
        return permissions.stream()
            .filter(permission -> permission.getPid() != null && permission.getPid().equals(parentId))
            .map(permission -> covert(permission, permissions)).collect(Collectors.toList());
    }

    @Override
    public void save(PermissionAO permissionAO) {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getName, permissionAO.getName());
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new ApplicationException("名称已存在");
        }
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionAO, permission);
        permission.setCreateTime(LocalDateTime.now());
        save(permission);
    }

    @Override
    public void update(PermissionAO permissionAO) {
        Permission permission = this.getById(permissionAO.getId());
        if (permission == null) {
            throw new ApplicationException("权限不存在");
        }
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getName, permissionAO.getName()).ne(Permission::getId, permissionAO.getId());
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new ApplicationException("名称已存在");
        }
        BeanUtils.copyProperties(permissionAO, permission);
        this.updateById(permission);
    }

    /**
     * 将权限转换为带有子级的权限对象 当找不到子级权限的时候map操作不会再递归调用covert
     */
    private PermissionNode covert(Permission permission, List<Permission> permissionList) {
        PermissionNode node = new PermissionNode();
        BeanUtils.copyProperties(permission, node);
        List<PermissionNode> children = permissionList.stream().filter(subPermission -> subPermission.getPid() != null
            && subPermission.getPid().equals(permission.getId())).map(subPermission ->
            covert(subPermission, permissionList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }

    @Override
    public void getChildNode(Permission permission, List<Permission> permissions,
        List<Permission> result) {
        List<Permission> childrens = permissions.stream()
            .filter(child -> child.getPid() != null && child.getPid().equals(permission.getId()))
            .collect(Collectors.toList());
        childrens.forEach(child -> getChildNode(child, permissions, result));
        result.addAll(childrens);
    }

    @Override
    public List<Permission> getUserAuthorityList(Long adminId) {
        List<Permission> allPermissions = list();
        List<Permission> permissionList = iAdminService.getPermissionList(adminId);
        List<Permission> childNodes;
        List<Permission> authorityList = new ArrayList<>();
        for (Permission per : permissionList) {
            childNodes = new ArrayList<>();
            getChildNode(per, allPermissions, childNodes);
            if (!hasChildNode(permissionList, childNodes)) {
                authorityList.add(per);
                authorityList.addAll(childNodes);
                getParentNode(per, allPermissions, authorityList);
            }
        }
        return authorityList;
    }

    @Override
    public void getParentNode(Permission permission, List<Permission> permissions, List<Permission> result) {
        Long parentId = permission.getPid();
        if (parentId != null) {
            Optional<Permission> parentOptional = permissions.stream().filter(per -> per.getId().equals(parentId))
                .findFirst();
            if (parentOptional.isPresent()) {
                result.add(parentOptional.get());
                getParentNode(parentOptional.get(), permissions, result);
            }
        }
    }

    private boolean hasChildNode(List<Permission> allPermissions, List<Permission> childNodes) {
        for (Permission permission : allPermissions) {
            for (Permission child : childNodes) {
                if (permission.getId().equals(child.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
