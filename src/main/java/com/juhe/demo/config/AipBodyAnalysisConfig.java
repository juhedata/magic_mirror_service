package com.juhe.demo.config;

import com.juhe.demo.api.AipBodyAnalysis;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "baidu")
@Data
public class AipBodyAnalysisConfig {

    private String appId;

    private String appKey;

    private String secretKey;

    @Bean
    public AipBodyAnalysis createAipBodyAnalysis() {

        return new AipBodyAnalysis(appId, appKey, secretKey);
    }

}
