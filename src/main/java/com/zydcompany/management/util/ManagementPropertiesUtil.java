package com.zydcompany.management.util;

import java.util.Properties;


public class ManagementPropertiesUtil {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    private static Properties managementBasicProperties;
    private static final String managementBasicFileName = "managementBasic.properties";
    private static Properties dataSourceProperties;
    private static final String dataSourceFileName = "dataSource.properties";

    static {

        managementBasicProperties = new Properties();
        dataSourceProperties = new Properties();

        try {
            managementBasicProperties.load(ClassLoader.getSystemResourceAsStream(managementBasicFileName));
            log.info("初始化managementBasic.properties成功");
            dataSourceProperties.load(ClassLoader.getSystemResourceAsStream(dataSourceFileName));
            log.info("初始化datasource.properties成功");
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

}
