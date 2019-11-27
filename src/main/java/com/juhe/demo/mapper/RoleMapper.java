package com.juhe.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface RoleMapper extends BaseMapper<Role> {


    @Select("SELECT p.id,p.pid,p.name,p.value,p.icon,p.type,p.uri,p.`status`,p.create_time,p.sort " +
        "FROM permission p INNER JOIN role_permission_relation r ON p.id = r.permission_id WHERE r.role_id = ${roleId}")
    List<Permission> getPermissionList(@Param("roleId") Long roleId);


}
