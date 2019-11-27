package com.juhe.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.juhe.demo.entity.Admin;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 后台用户表 Mapper 接口
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface AdminMapper extends BaseMapper<Admin> {


    @Select("SELECT r.id,r.code,r.name,r.description,r.create_time,r.`status`,r.sort " +
        "FROM role r INNER JOIN admin_role_relation a ON r.id = a.role_id WHERE a.admin_id= ${adminId}")
    List<Role> getRoleList(@Param("adminId") Long adminId);


    @Select("SELECT p.id,p.pid,p.name,p.value,p.icon,p.type,p.uri,p.`status`,p.create_time,p.sort " +
        "FROM permission p INNER JOIN role_permission_relation r on p.id = r.permission_id " +
        "INNER JOIN admin_role_relation a ON r.role_id = a.role_id WHERE " +
        "a.admin_id = ${adminId}")
    List<Permission> getPermissionList(@Param("adminId") Long adminId);

}
