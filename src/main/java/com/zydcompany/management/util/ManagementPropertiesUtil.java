package com.zydcompany.management.util;

import java.util.Properties;


public class ManagementPropertiesUtil {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    private static Properties managementBasicProperties;
    private static final String managementBasicFileName = "managementBasic.properties";
    private static Properties dataSourceProperties;
    private static final String dataSourceFileName = "dataSource.properties";
    private static Properties redisProperties;
    private static final String redisFileName = "redis.properties";
    private static Properties ssdbProperties;
    private static final String ssdbFileName = "ssdb.properties";

    static {

        managementBasicProperties = new Properties();
        dataSourceProperties = new Properties();
        redisProperties = new Properties();
        ssdbProperties = new Properties();

        try {
            managementBasicProperties.load(ClassLoader.getSystemResourceAsStream(managementBasicFileName));
            log.info("初始化managementBasic.properties成功");
            dataSourceProperties.load(ClassLoader.getSystemResourceAsStream(dataSourceFileName));
            log.info("初始化datasource.properties成功");
            redisProperties.load(ClassLoader.getSystemResourceAsStream(redisFileName));
            log.info("初始化redis.properties成功");
            ssdbProperties.load(ClassLoader.getSystemResourceAsStream(ssdbFileName));
            log.info("ssdb.properties成功");
        } catch (Exception e) {
            log.info("ManagementPropertiesUtil初始化失败", e);
        }
    }

    public static String getManagementBasicPropertiesValue(String key) {
        return managementBasicProperties.getProperty(key);
    }

    public static String getDatasourcePropertiesValue(String key) {
        return dataSourceProperties.getProperty(key);
    }

    public static String getRedisPropertiesValue(String key) {
        return redisProperties.getProperty(key);
    }

    public static String getSsdbPropertiesValue(String key) {
        return ssdbProperties.getProperty(key);
    }

}
