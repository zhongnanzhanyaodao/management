package com.zydcompany.management.config.ssdb;


import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.nutz.ssdb4j.impl.SimpleClient;
import org.nutz.ssdb4j.pool.PoolSSDBStream;
import org.nutz.ssdb4j.pool.Pools;
import org.nutz.ssdb4j.spi.SSDB;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SsdbPool {
    private static Map<String, PoolSSDBStream> poolSSDBStreamMap = new ConcurrentHashMap<>();
    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    public static SSDB ssdbInstance(String poolName) {
        if (!poolSSDBStreamMap.containsKey(poolName)) {
            initPoolSSDBStream(poolName);
        }
        PoolSSDBStream poolSSDBStream = poolSSDBStreamMap.get(poolName);
        return new SimpleClient(poolSSDBStream);
    }

    public static synchronized void initPoolSSDBStream(String poolName) {
        String host = ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.host");
        Integer port = Integer.parseInt(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.port"));
        Integer timeout = Integer.parseInt(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.timeout"));
        Integer maxActive = Integer.parseInt(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.maxActive"));
        Integer minIdle = Integer.parseInt(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.minIdle"));
        Integer maxIdle = Integer.parseInt(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.maxIdle"));
        Integer maxWait = Integer.parseInt(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.maxWait"));
        Boolean testWhileIdle = Boolean.parseBoolean(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.testWhileIdle"));
        Integer timeBetweenEvictionRunsMillis = Integer.parseInt(ManagementPropertiesUtil.getSsdbPropertiesValue("ssdb.timeBetweenEvictionRunsMillis"));
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = maxActive;
        config.minIdle = minIdle;
        config.maxIdle = maxIdle;
        config.maxWait = maxWait;
        config.testWhileIdle = testWhileIdle;
        config.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        PoolSSDBStream poolSSDBStream = Pools.pool(host, port, timeout, config, null);
        poolSSDBStreamMap.put(poolName, poolSSDBStream);
        log.info("init PoolSSDBStream successful");
    }


}
