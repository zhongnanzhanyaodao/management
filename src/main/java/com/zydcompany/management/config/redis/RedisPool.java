package com.zydcompany.management.config.redis;


import com.zydcompany.management.common.constant.SymbolConstant;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisPool {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    private static Map<String, JedisPool> jedisPoolMap = new ConcurrentHashMap<>();

    private static String REDIS = "redis";
    private static String HOST = "host";
    private static String PASSWORD = "password";
    private static String DATABASE = "database";
    private static String TIMEOUT = "timeout";
    private static String PORT = "port";
    private static String MAXTOTAL = "maxTotal";
    private static String MAXIDLE = "maxIdle";
    private static String MINIDLE = "minIdle";
    private static String MAXWAIT = "maxWait";
    private static String TESTONBORROW = "testOnBorrow";
    private static String TESTONRETURN = "testOnReturn";
    private static String TESTWHILEIDLE = "testWhileIdle";
    private static String TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";

    public static Jedis jedisInstance(String poolName) {
        if (!jedisPoolMap.containsKey(poolName)) {
            initJedisPool(poolName);
        }
        Jedis jedis = jedisPoolMap.get(poolName).getResource();
        return jedis;
    }

    /**
     * 若redis配置文件读取不到，抛出string转int异常提醒
     */
    private static synchronized void initJedisPool(String poolName) {
        String redisPropertyPrefix = REDIS + SymbolConstant.DOT + poolName;
        String host = ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + HOST);
        String password = ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + PASSWORD);
        int database = Integer.parseInt(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + DATABASE));
        int timeout = Integer.parseInt(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + TIMEOUT));
        int port = Integer.parseInt(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + PORT));
        int maxTotal = Integer.parseInt(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + MAXTOTAL));
        int maxIdle = Integer.parseInt(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + MAXIDLE));
        int minIdle = Integer.parseInt(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + MINIDLE));
        long maxWait = Long.parseLong(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + MAXWAIT));
        boolean testOnBorrow = Boolean.parseBoolean(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + TESTONBORROW));
        boolean testOnReturn = Boolean.parseBoolean(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + TESTONRETURN));
        boolean testWhileIdle = Boolean.parseBoolean(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + TESTWHILEIDLE));
        long timeBetweenEvictionRunsMillis = Long.parseLong(ManagementPropertiesUtil.getRedisPropertiesValue(redisPropertyPrefix + SymbolConstant.DOT + TIMEBETWEENEVICTIONRUNSMILLIS));

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setTestWhileIdle(testWhileIdle);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        //防止并发问题,保证只实例化一个连接池
        if (!jedisPoolMap.containsKey(poolName)) {
            JedisPool jedisPool = new JedisPool(config, host, port, timeout, password, database);
            jedisPoolMap.put(poolName, jedisPool);
            log.info("init real jedisPool successful");
        }
        log.info("init jedisPool successful");

    }
}