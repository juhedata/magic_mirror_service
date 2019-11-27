package com.juhe.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juhe.demo.ao.PermissionAO;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.vo.PermissionNode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 权限菜单表 服务类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-18
 */
public interface IPermissionService extends IService<Permission> {

    /**
     * @return void
     * @Author xxmfypp
     * @Description 根据权限编号批量删除权限
     * @Date 17:31 2019/7/18
     * @Param [ids]
     **/
    @Transactional
    void delete(List<Long> ids);


    /**
     * @return java.util.List<com.juhe.demo.vo.PermissionNode>
     * @Author xxmfypp
     * @Description 获取树状结构的权限表
     * @Date 17:45 2019/7/18
     * @Param []
     **/
    List<PermissionNode> treeList();

    /**
     * @Description 根据父节点获取树状结构的权限表
     */
    List<PermissionNode> treeListByParentId(Long parentId);

    /**
     * @MethodName: save
     * @Description: 新增权限
     * @Param: [permissionAO]
     * @Return: void
     * @Author: luxianzhu
     * @Date: 2019/8/8
     */
    void save(PermissionAO permissionAO);

    /**
     * @MethodName: update
     * @Description: 更新权限
     * @Param: [permissionAO]
     * @Return: void
     * @Author: luxianzhu
     * @Date: 2019/8/8
     */
    void update(PermissionAO permissionAO);

    /**
     * @MethodName: getChildNode
     * @Description: 获取所有子节点
     * @Param: [permission, permissions, result]
     * @Return: void
     * @Author: luxianzhu
     * @Date: 2019/8/12
     */
    void getChildNode(Permission permission, List<Permission> permissions,
        List<Permission> result);

    /**
     * @MethodName: getParentNode
     * @Description: 获取所有父节点
     * @Param: [permission, permissions, result]
     * @Return: void
     * @Author: luxianzhu
     * @Date: 2019/8/12
     */
    void getParentNode(Permission permission, List<Permission> permissions,
        List<Permission> result);

    /**
     * @MethodName: getUserAuthorityList
     * @Description: 获取用户权限
     * @Param: [adminId]
     * @Return: java.util.List<com.juhe.demo.entity.Permission>
     * @Author: luxianzhu
     * @Date: 2019/8/12
     */
    List<Permission> getUserAuthorityList(Long adminId);

}
