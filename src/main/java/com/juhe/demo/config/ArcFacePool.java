package com.juhe.demo.config;

import com.juhe.demo.api.ArcFaceAnalysis;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @CLassName ArcFacePool
 * @Description TODO
 * @Author kuai.zhang
 * @Date 2019/11/14 11:16
 * @Version 1.0
 **/
public class ArcFacePool extends GenericObjectPool<ArcFaceAnalysis> {

    public ArcFacePool(PooledObjectFactory<ArcFaceAnalysis> factory) {
        super(factory);
    }

    public ArcFacePool(PooledObjectFactory<ArcFaceAnalysis> factory,
        GenericObjectPoolConfig<ArcFaceAnalysis> config) {
        super(factory, config);
    }

    public ArcFacePool(PooledObjectFactory<ArcFaceAnalysis> factory,
        GenericObjectPoolConfig<ArcFaceAnalysis> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
