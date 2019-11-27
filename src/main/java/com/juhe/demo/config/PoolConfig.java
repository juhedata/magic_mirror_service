package com.juhe.demo.config;

import com.juhe.demo.api.ArcFaceAnalysis;
import com.juhe.demo.constant.SystemConstant;
import javax.annotation.PreDestroy;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @CLassName PoolConfig
 * @Description TODO
 * @Author kuai.zhang
 * @Date 2019/11/14 11:21
 * @Version 1.0
 **/
@Configuration
public class PoolConfig {

    private ArcFacePool pool;

    @Autowired
    private ArcFaceProperties arcFaceProperties;

    @ConditionalOnClass({ArcFaceFactory.class})
    @Bean
    protected ArcFacePool faceSDKPool() {
        ArcFaceFactory faceSDKFactory = new ArcFaceFactory(arcFaceProperties);
        //设置对象池的相关参数
        GenericObjectPoolConfig<ArcFaceAnalysis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(SystemConstant.PoolProperties.maxIdle);
        poolConfig.setMaxTotal(SystemConstant.PoolProperties.maxTotal);
        poolConfig.setMinIdle(SystemConstant.PoolProperties.minIdle);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30);
        //一定要关闭jmx，不然springboot启动会报已经注册了某个jmx的错误
        poolConfig.setJmxEnabled(false);
        //新建一个对象池,传入对象工厂和配置
        pool = new ArcFacePool(faceSDKFactory, poolConfig);
        initPool(SystemConstant.PoolProperties.initialSize, SystemConstant.PoolProperties.maxIdle);
        return pool;
    }

    /**
     * 预先加载对象到对象池中
     *
     * @param initialSize 初始化连接数
     * @param maxIdle 最大空闲连接数
     */
    private void initPool(int initialSize, int maxIdle) {
        if (initialSize <= 0) {
            return;
        }
        int size = Math.min(initialSize, maxIdle);
        for (int i = 0; i < size; i++) {
            try {
                pool.addObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (pool != null) {
            pool.close();
        }
    }
}
