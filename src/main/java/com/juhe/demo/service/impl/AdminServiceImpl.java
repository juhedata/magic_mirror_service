package com.juhe.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juhe.demo.ApplicationException;
import com.juhe.demo.ao.AdminAO;
import com.juhe.demo.ao.PasswordAO;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.common.ResultCodeEnum;
import com.juhe.demo.component.JwtTokenUtil;
import com.juhe.demo.entity.Admin;
import com.juhe.demo.entity.AdminRoleRelation;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.entity.Role;
import com.juhe.demo.mapper.AdminMapper;
import com.juhe.demo.mapper.AdminRoleRelationMapper;
import com.juhe.demo.service.IAdminService;
import com.juhe.demo.vo.AdminVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Resource
    private AdminRoleRelationMapper adminRoleRelationMapper;

    /**
     * @return java.lang.String
     * @Author xuman.xu
     * @Description 用户登录，生成TOKEN，并保存登录时间
     * @Date 11:08 2019/7/17
     * @Param [username, password]
     */
    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
            updateLoginTimeByUsername(username);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;

    }

    private void updateLoginTimeByUsername(String username) {
        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("login_time", LocalDateTime.now());
        updateWrapper.eq("username", username);
        update(updateWrapper);
    }

    /**
     * @return com.juhe.demo.entity.Admin
     * @Author xuman.xu
     * @Description 根据用户名获取管理信息
     * @Date 13:13 2019/7/17
     * @Param [username]
     */
    @Override
    public Admin getAdminByUserName(String username) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }


    @Override
    public String refreshToken(String oldToken) {
        if (StringUtils.isEmpty(oldToken)) {
            return null;
        }
        String token = oldToken.substring(tokenHead.length());
        if (jwtTokenUtil.canRefresh(token)) {
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }

    @Override
    public boolean deleteAdminById(Long id) {
        QueryWrapper<AdminRoleRelation> adminRoleWrapper = new QueryWrapper<>();
        adminRoleWrapper.lambda().eq(AdminRoleRelation::getAdminId, id);
        adminRoleRelationMapper.delete(adminRoleWrapper);
        return removeById(id);
    }

    @Override
    public void updateRole(Long adminId, List<Long> roleIds) {
        QueryWrapper<AdminRoleRelation> adminRoleWrapper = new QueryWrapper<>();
        adminRoleWrapper.lambda().eq(AdminRoleRelation::getAdminId, adminId);
        //获取当前角色所有的权限
        List<AdminRoleRelation> adminRoleRelationList = adminRoleRelationMapper.selectList(adminRoleWrapper);
        List<Long> oldRoleIds = new ArrayList<>();
        if (adminRoleRelationList != null && !adminRoleRelationList.isEmpty()) {
            for (AdminRoleRelation adminRoleRelation : adminRoleRelationList) {
                oldRoleIds.add(adminRoleRelation.getRoleId());
            }
        }
        //需要插入的权限编号
        List<Long> needAddRoleIds = new ArrayList<>();
        for (Long id : roleIds) {
            if (!oldRoleIds.contains(id)) {
                needAddRoleIds.add(id);
            } else {
                //删除已存在的权限编号,剩余需要删除的权限编号
                oldRoleIds.remove(id);
            }
        }
        //删除权限
        if (!oldRoleIds.isEmpty()) {
            for (Long id : oldRoleIds) {
                QueryWrapper<AdminRoleRelation> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(AdminRoleRelation::getAdminId, adminId)
                    .eq(AdminRoleRelation::getRoleId, id);
                adminRoleRelationMapper.delete(queryWrapper);
            }
        }
        //增加权限
        if (!needAddRoleIds.isEmpty()) {
            for (Long id : needAddRoleIds) {
                AdminRoleRelation adminRoleRelation = new AdminRoleRelation();
                adminRoleRelation.setAdminId(adminId);
                adminRoleRelation.setRoleId(id);
                adminRoleRelationMapper.insert(adminRoleRelation);
            }
        }
    }

    @Override
    public List<Role> getRoleList(Long adminId) {
        return baseMapper.getRoleList(adminId);
    }

    @Override
    public List<Permission> getPermissionList(Long adminId) {
        return baseMapper.getPermissionList(adminId);
    }

    @Override
    public List<AdminVO> getAdminList(String name) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.like(Admin::getUsername, name.trim()).or().like(Admin::getNickName, name);
        }
        List<Admin> admins = list(queryWrapper);
        List<AdminVO> adminVOs = new ArrayList<>(admins.size());
        AdminVO adminVO;
        for (Admin admin : admins) {
            adminVO = new AdminVO();
            BeanUtils.copyProperties(admin, adminVO);
            getAdminRoleInfo(adminVO);
            adminVOs.add(adminVO);
        }
        return adminVOs;
    }

    @Override
    public AdminVO getAdminById(Long adminId) {
        Admin admin = getById(adminId);
        if (admin == null) {
            return null;
        }
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);
        getAdminRoleInfo(adminVO);
        return adminVO;
    }

    @Override
    public Boolean saveAdmin(AdminAO adminAO) {
        if (StringUtils.isEmpty(adminAO.getPassword())) {
            return false;
        }
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, adminAO.getUsername()).or().eq(Admin::getEmail, adminAO.getEmail());
        int count = count(queryWrapper);
        if (count > 0) {
            throw new ApplicationException("用户名或者邮箱已存在");
        }
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminAO, admin, "id");
        admin.setCreateTime(LocalDateTime.now());

        String password = passwordEncoder.encode(adminAO.getPassword());
        admin.setPassword(password);
        save(admin);
        saveAdminRoleRelation(admin.getId(), adminAO.getRoleId());
        return true;
    }

    @Override
    public Boolean updateAdmin(AdminAO adminAO) {
        Admin admin = getById(adminAO.getId());
        if (admin == null) {
            return false;
        }
        LambdaQueryWrapper<Admin> existQueryWrapper = new LambdaQueryWrapper<>();
        existQueryWrapper.ne(Admin::getId, admin.getId()).and(
            query -> query.eq(Admin::getUsername, adminAO.getUsername()).or().eq(Admin::getEmail, adminAO.getEmail()));
        int count = this.count(existQueryWrapper);
        if (count > 0) {
            throw new ApplicationException("用户名或者邮箱已存在");
        }
        LambdaQueryWrapper<AdminRoleRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleRelation::getAdminId, adminAO.getId());
        adminRoleRelationMapper.delete(queryWrapper);
        BeanUtils.copyProperties(adminAO, admin, "password");
        saveOrUpdate(admin);
        saveAdminRoleRelation(adminAO.getId(), adminAO.getRoleId());
        return true;
    }

    @Override
    public AdminVO getAdminInfoByToken(String token) {
        String name = jwtTokenUtil.getUserNameFromToken(token);
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, name);
        Admin admin = getOne(queryWrapper);
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);
        getAdminRoleInfo(adminVO);
        return adminVO;
    }

    @Override
    public void updatePassword(Long id, PasswordAO passwordAO) {
        Admin admin = this.getById(id);
        if (admin == null) {
            throw new ApplicationException("用户未找到");
        }
        boolean matchPassword = passwordEncoder.matches(passwordAO.getOldPassword(), admin.getPassword());
        if (!matchPassword) {
            throw new ApplicationException("原密码不正确");
        }

        String newPassword = passwordEncoder.encode(passwordAO.getNewPassword());
        admin.setPassword(newPassword);
        saveOrUpdate(admin);

    }

    /**
     * @description 获取管理员的角色信息
     * @author xianzhu.lu
     */
    private void getAdminRoleInfo(AdminVO adminVO) {
        List<Role> roles = getRoleList(adminVO.getId());
        Long[] roleIdArray = new Long[roles.size()];
        String[] roleNameArray = new String[roles.size()];
        if (!roles.isEmpty()) {
            for (int index = 0; index < roles.size(); index++) {
                roleIdArray[index] = roles.get(index).getId();
                roleNameArray[index] = roles.get(index).getName();
            }
        }
        adminVO.setRoleId(roleIdArray);
        adminVO.setRoleName(roleNameArray);
    }

    /**
     * @author xianzhu.lu
     * @description 保存用户角色信息
     */
    private void saveAdminRoleRelation(Long adminId, Long[] roleId) {
        if (roleId != null && roleId.length > 0) {
            AdminRoleRelation adminRoleRelation;
            for (Long id : roleId) {
                adminRoleRelation = new AdminRoleRelation();
                adminRoleRelation.setAdminId(adminId);
                adminRoleRelation.setRoleId(id);
                adminRoleRelationMapper.insert(adminRoleRelation);
            }
        }
    }
}
