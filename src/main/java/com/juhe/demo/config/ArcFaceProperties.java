package com.juhe.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @CLassName ArcFaceProperties
 * @Description 池属性配置
 * @Author kuai.zhang
 * @Date 2019/11/14 14:21
 * @Version 1.0
 **/
@Configuration
@ConfigurationProperties(prefix = "arc.face")
@Data
public class ArcFaceProperties {

    private String appId;

    private String sdkKey;

    private String lib;

    private float similarValue;

}
