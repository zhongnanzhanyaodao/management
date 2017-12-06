package com.zydcompany.management.config.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据源配置
 */
@Configuration
public class MybatisDataSourceConfig {

    //https://github.com/alibaba/druid
    @Bean
    public DataSource dataSource() {
        String driverClassName = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.driverClassName");
        String url = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.url");
        String userName = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.userName");
        String password = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.password");
        //连接初始值,连接池启动时创建的连接数量的初始值
        String initialSize = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.initialSize");
        //最小空闲值,当空闲的连接数少于阀值时，连接池就会预申请去一些连接，以免洪峰来时来不及申请
        String minIdle = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.minIdle");
        //最大空闲值.当经过一个高峰时间后，连接池可以慢慢将已经用不到的连接慢慢释放一部分，一直减少到maxIdle为止 ，已经不再使用，配置了也没效果
        String maxIdle = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.maxIdle");
        //连接池的最大值,同一时间可以从池分配的最多连接数量
        String maxActive = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.maxActive");
        //获取连接时最大等待时间，单位毫秒
        String maxWaitMillis = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.maxWaitMillis");
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize == null ? 2 : Integer.parseInt(initialSize));
        dataSource.setMinIdle(minIdle == null ? 2 : Integer.parseInt(minIdle));
        dataSource.setMaxActive(maxActive == null ? 20 : Integer.parseInt(maxActive));
        dataSource.setMaxWait(maxWaitMillis == null ? 60000L : Long.parseLong(maxWaitMillis));
        return dataSource;
    }

}
