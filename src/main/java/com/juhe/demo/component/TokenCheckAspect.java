package com.juhe.demo.component;

import com.juhe.demo.common.CommonResult;
import com.juhe.demo.constant.SystemConstant.PermitRequest;
import com.juhe.demo.service.IAdminService;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @CLassName TokenCheckAspect
 * @Description token 快要过期切面处理
 * @Author xuman.xu
 * @Date 2019/7/16 18:02
 * @Version 1.0
 **/
@Aspect
@Component
@Order(1)
public class TokenCheckAspect {

    //刷新token时间间隔
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Pointcut("execution(public * com.juhe.demo.controller.*.*(..))")
    public void TokenCheck() {
    }

    @Around("TokenCheck()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader(tokenHeader);
        String newToken = null;
        if (!Arrays.asList(PermitRequest.MATCH_PERMIT_PATH).contains(request.getServletPath())
            && StringUtils.isNotEmpty(token) && token.startsWith(tokenHead)) {
            Claims claims = jwtTokenUtil.getClaimsFromToken(token.substring(tokenHead.length()));
            if (claims != null) {
                Date expiredDate = claims.getExpiration();
                //距离token过期时间三分之一时间内刷新token
                if (expiredDate.getTime() - System.currentTimeMillis() < expiration * 1000 / 3) {
                    claims.put(CLAIM_KEY_CREATED, new Date());
                    newToken = jwtTokenUtil.refreshToken(claims);
                }
            }
        }
        Object result = joinPoint.proceed();
        if (result instanceof CommonResult && StringUtils.isNotEmpty(newToken)) {
            CommonResult commonResult = (CommonResult) result;
            commonResult.setToken(tokenHead + " " + newToken);
        }
        return result;
    }
}