package com.juhe.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juhe.demo.ao.AdminAO;
import com.juhe.demo.ao.PasswordAO;
import com.juhe.demo.entity.Admin;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import com.juhe.demo.vo.AdminVO;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface IAdminService extends IService<Admin> {

    /**
     * @return java.lang.String
     * @Author xuman.xu
     * @Description 用户登录，生成TOKEN，并保存登录时间
     * @Date 11:08 2019/7/17
     * @Param [username, password]
     **/
    @Transactional
    String login(String username, String password);

    /**
     * @return com.juhe.demo.entity.Admin
     * @Author xuman.xu
     * @Description 根据用户名获取管理信息
     * @Date 13:13 2019/7/17
     * @Param [username]
     **/
    Admin getAdminByUserName(String username);


    /**
     * @return java.lang.String
     * @Author xxmfypp
     * @Description 刷新登录的Token
     * @Date 15:21 2019/7/18
     * @Param [oldToken]
     **/
    String refreshToken(String oldToken);


    /**
     * @return boolean
     * @Author xxmfypp
     * @Description 删除管理员用户角色信息
     * @Date 15:52 2019/7/18
     * @Param [id]
     **/
    @Transactional
    boolean deleteAdminById(Long id);

    /**
     * @return void
     * @Author xxmfypp
     * @Description 分配用户角色
     * @Date 16:06 2019/7/18
     * @Param [adminId, roleIds]
     **/
    @Transactional
    void updateRole(Long adminId, List<Long> roleIds);


    /**
     * @return java.util.List<com.juhe.demo.entity.Role>
     * @Author xxmfypp
     * @Description 获取用户角色
     * @Date 16:14 2019/7/18
     * @Param [adminId]
     **/
    List<Role> getRoleList(Long adminId);


    /**
     * @return java.util.List<com.juhe.demo.entity.Permission>
     * @Author xxmfypp
     * @Description 获取用户权限
     * @Date 16:23 2019/7/18
     * @Param [adminId]
     **/
    List<Permission> getPermissionList(Long adminId);

    /**
     * @return java.util.List<com.juhe.demo.vo.AdminVO>
     * @Author xianzhu.lu
     * @Description 获取所有管理用户信息
     * @Date 2019/8/1
     */
    List<AdminVO> getAdminList(String name);

    /**
     * @return com.juhe.demo.vo.AdminVO
     * @Author xianzhu.lu
     * @Description 获取单个管理用户信息
     * @Date 2019/8/1
     */
    AdminVO getAdminById(Long adminId);

    /**
     * @return java.lang.Boolean
     * @Author xianzhu.lu
     * @Description 新增管理员信息
     * @Date 2019/8/1
     */
    Boolean saveAdmin(AdminAO adminAO);

    /**
     * @return java.lang.Boolean
     * @Author xianzhu.lu
     * @Description 修改管理员信息
     * @Date 2019/8/1
     */
    Boolean updateAdmin(AdminAO adminAO);

    /**
     * @return com.juhe.demo.vo.AdminVO
     * @Author xianzhu.lu
     * @Description 根据token获取当前登录用户信息
     * @Date 2019/8/2
     */
    AdminVO getAdminInfoByToken(String token);

    /**
     * @Author xianzhu.lu
     * @Description 根修改用户密码
     * @Date 2019/8/7
     */
    void updatePassword(Long id, PasswordAO passwordAO);

}
