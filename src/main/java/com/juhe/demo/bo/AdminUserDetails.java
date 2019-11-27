package com.juhe.demo.bo;

import com.juhe.demo.entity.Admin;
import com.juhe.demo.entity.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
/**
 * @CLassName AdminUserDetails
 * @Description TODO
 * @Author xuman.xu
 * @Date 2019/7/17 11:54
 * @Version 1.0
 **/
public class AdminUserDetails implements UserDetails {

    private Admin admin;

    private List<Permission> permissionList;

    public AdminUserDetails(Admin admin, List<Permission> permissionList) {
        this.admin = admin;
        this.permissionList = permissionList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        if (permissionList != null) {
            for (Permission authority : permissionList) {
                grantedAuthorityList.add(new SimpleGrantedAuthority(authority.getValue()));
            }
        }
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}