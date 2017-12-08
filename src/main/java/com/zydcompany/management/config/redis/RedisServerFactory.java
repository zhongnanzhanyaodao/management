package com.zydcompany.management.config.redis;


public class RedisServerFactory {

    private static final String DEAFAULT_POOL_NAME = "pool";

    public static RedisServer getRedisServer(String poolName) {
        return RedisServer.redisServerInstance(poolName);
    }

    public static RedisServer getRedisServer() {
        return RedisServer.redisServerInstance(DEAFAULT_POOL_NAME);
    }
}
