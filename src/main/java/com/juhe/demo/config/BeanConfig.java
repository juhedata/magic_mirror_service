package com.juhe.demo.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @CLassName BeanConfig
 * @Description Bean配置相关
 * @Author kuai.zhang
 * @Date 2019/7/19 17:38
 * @Version 1.0
 **/
@Configuration
public class BeanConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
