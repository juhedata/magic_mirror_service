package com.juhe.demo.config;


import com.juhe.demo.api.ArcFaceAnalysis;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @CLassName LpPoolFactory
 * @Description TODO
 * @Author kuai.zhang
 * @Date 2019/11/14 10:57
 * @Version 1.0
 **/
public class ArcFaceFactory implements PooledObjectFactory<ArcFaceAnalysis> {

    private ArcFaceProperties arcFaceProperties;

    public ArcFaceFactory() {
    }

    public ArcFaceFactory(ArcFaceProperties arcFaceProperties) {
        this.arcFaceProperties = arcFaceProperties;
    }

    @Override
    public PooledObject<ArcFaceAnalysis> makeObject() throws Exception {
        ArcFaceAnalysis arcFaceAnalysis = new ArcFaceAnalysis(arcFaceProperties.getAppId(),
            arcFaceProperties.getSdkKey(),
            arcFaceProperties.getLib(), arcFaceProperties.getSimilarValue());
        return new DefaultPooledObject<>(arcFaceAnalysis);
    }

    @Override
    public void destroyObject(PooledObject<ArcFaceAnalysis> pooledObject) throws Exception {
        ArcFaceAnalysis arcFaceAnalysis = pooledObject.getObject();
        if (arcFaceAnalysis != null) {
            arcFaceAnalysis.unInit();
        }
    }

    @Override
    public boolean validateObject(PooledObject<ArcFaceAnalysis> pooledObject) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<ArcFaceAnalysis> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<ArcFaceAnalysis> pooledObject) throws Exception {

    }
}
