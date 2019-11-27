package com.juhe.demo.config;

import com.juhe.demo.component.JwtAuthenticationTokenFilter;
import com.juhe.demo.component.RestAuthenticationEntryPoint;
import com.juhe.demo.component.RestfulAccessDeniedHandler;
import com.juhe.demo.constant.SystemConstant.PermitRequest;
import com.juhe.demo.entity.Admin;
import com.juhe.demo.entity.Permission;
import com.juhe.demo.service.IAdminService;
import com.juhe.demo.service.IPermissionService;
import com.juhe.demo.vo.PermissionNode;
import com.juhe.demo.vo.PermissionVO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @CLassName SecurityConfig
 * @Description SpringSecurity配置
 * @Author xuman.xu
 * @Date 2019/7/16 11:18
 * @Version 1.0
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private IAdminService iAdminService;

    @Autowired
    private IPermissionService iPermissionService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().logout().disable().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // 允许对于网站静态资源的无授权访问
            .and().authorizeRequests().antMatchers(HttpMethod.GET,
            "/*.html",
            "/favicon.ico",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/swagger-resources/**",
            "/v2/api-docs/**").permitAll()
            // 对登录注册要允许匿名访问
            .antMatchers(PermitRequest.MATCH_PERMIT_PATH)
            .permitAll()
            //跨域请求会先进行一次options请求
            .antMatchers(HttpMethod.OPTIONS)
            .permitAll()
            //测试时全部运行访问
            //.antMatchers("/**")
            //.permitAll()
            .anyRequest()
            // 除上面外的所有请求全部需要鉴权认证
            .authenticated();
        // 禁用缓存
        http.headers().cacheControl();
        // 添加JWT filter
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //添加自定义未授权和未登录结果返回
        http.exceptionHandling()
            .accessDeniedHandler(restfulAccessDeniedHandler)
            .authenticationEntryPoint(restAuthenticationEntryPoint);
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> {
            UserBuilder userBuilder = User.builder();
            Admin admin = iAdminService.getAdminByUserName(username);
            if (admin != null) {
                userBuilder.username(username).password(admin.getPassword()).accountExpired(false)
                    .accountLocked(false).disabled(false).credentialsExpired(false);
                List<Permission> permissionList = iPermissionService.getUserAuthorityList(admin.getId());
                List<String> grantedAuthorityList = new ArrayList<>();
                if (permissionList != null) {
                    grantedAuthorityList = permissionList.stream().filter(p -> StringUtils.isNotBlank(p.getValue()))
                        .map(Permission::getValue).collect(Collectors.toList());
                }
                return userBuilder.authorities(grantedAuthorityList.stream().toArray(String[]::new)).build();
            }
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }

    /**
     * 获取所有叶子节点权限
     */
    private Set<String> getLeafNodeList(Set<String> values, List<PermissionNode> permissionNodes) {
        permissionNodes.stream()
            .filter(permission -> permission.getChildren() != null && !permission.getChildren().isEmpty())
            .map(permission -> getLeafNodeList(values, permission.getChildren()))
            .forEach(values::addAll);

        Set<String> permissionValues = permissionNodes.stream()
            .filter(permission -> permission.getChildren() == null || permission.getChildren().isEmpty())
            .map(PermissionVO::getValue).collect(
                Collectors.toSet());
        values.addAll(permissionValues);
        return values;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
            .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 允许跨域调用的过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return new CorsFilter(source);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

}
