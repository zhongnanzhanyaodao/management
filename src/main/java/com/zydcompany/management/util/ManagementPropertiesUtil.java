package com.zydcompany.management.util;

import java.util.Collection;
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
    private static Properties zookeeperProperties;
    private static final String zookeeperFileName = "zookeeper.properties";
    private static Properties threadProperties;
    private static final String threadFileName = "thread.properties";
    private static Properties proxyUrlProperties;
    private static final String proxyUrlFileName = "proxyUrl.properties";

    static {

        managementBasicProperties = new Properties();
        dataSourceProperties = new Properties();
        redisProperties = new Properties();
        ssdbProperties = new Properties();
        zookeeperProperties = new Properties();
        threadProperties = new Properties();
        proxyUrlProperties = new Properties();

        try {
            managementBasicProperties.load(ClassLoader.getSystemResourceAsStream(managementBasicFileName));
            log.info("初始化managementBasic.properties成功");
            dataSourceProperties.load(ClassLoader.getSystemResourceAsStream(dataSourceFileName));
            log.info("初始化datasource.properties成功");
            redisProperties.load(ClassLoader.getSystemResourceAsStream(redisFileName));
            log.info("初始化redis.properties成功");
            ssdbProperties.load(ClassLoader.getSystemResourceAsStream(ssdbFileName));
            log.info("初始化ssdb.properties成功");
            zookeeperProperties.load(ClassLoader.getSystemResourceAsStream(zookeeperFileName));
            log.info("初始化zookeeper.properties成功");
            threadProperties.load(ClassLoader.getSystemResourceAsStream(threadFileName));
            log.info("初始化thread.properties成功");
            proxyUrlProperties.load(ClassLoader.getSystemResourceAsStream(proxyUrlFileName));
            log.info("初始化proxyUrl.properties成功");
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

    public static String getZookeeperPropertiesValue(String key) {
        return zookeeperProperties.getProperty(key);
    }

    public static String getThreadPropertiesValue(String key) {
        return threadProperties.getProperty(key);
    }

    public static Collection<Object> getProxyUrlPropertiesValues() {
        return proxyUrlProperties.values();
    }

    //=====================================reload when file change========================================

    /**
     * managementBasic.properties
     */
    public static void managementBasicPropReload() {

        Properties managementBasicPropertiesTmp = new Properties();
        try {
            managementBasicPropertiesTmp.load(ClassLoader.getSystemResourceAsStream(managementBasicFileName));
            managementBasicProperties = managementBasicPropertiesTmp;
        } catch (Exception e) {
            log.error("ManagementPropertiesUtil reload managementBasic.properties Exception", e);
        }
        log.info("ManagementPropertiesUtil reload managementBasic.properties success");
    }
}
