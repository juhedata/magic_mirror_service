package com.juhe.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @CLassName MyBatisConfig
 * @Description MyBatis配置
 * @Author xuman.xu
 * @Date 2019/7/16 11:16
 * @Version 1.0
 **/
@Configuration
@EnableTransactionManagement
@MapperScan("com.juhe.demo.mapper")
public class MyBatisConfig {
}