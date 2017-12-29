package com.zydcompany.management.config.mybatis.sharding;

public class ShardingHelper {
    /**
     * 获取hashcode并使得>0
     *
     * @param key   取模key
     * @param count 取模范围
     * @return
     */
    public static final Integer hashCode(String key, Integer count) {
        return Math.abs(key.hashCode()) % count;
    }

    /**
     * 转换字符串
     *
     * @param key
     * @param count
     * @return
     */
    public static final String hashCodeStr(String key, Integer count) {
        return Integer.toString(hashCode(key, count));
    }

    /**
     * 根据hash分表
     *
     * @param key
     * @param count
     * @return
     */
    public static String getShardingByHash(String key, int count) {
        return hashCodeStr(key, count);
    }


    /**
     * 获取key对应的表
     *
     * @param key
     * @param count
     * @return
     */
    public static String getSharding(String key, int count) {
        return getShardingByHash(key, count);
    }


}
