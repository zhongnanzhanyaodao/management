package com.zydcompany.management.config.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingjdbc.core.constant.ShardingPropertiesConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源配置
 */
@Configuration
public class MybatisDataSourceConfig {


    public DataSource initOriginDataSource0() {
        String url = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.url.0");
        String userName = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.userName.0");
        String password = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.password.0");
        return getDruidDataSource(url, userName, password);
    }

    public DataSource initOriginDataSource1() {
        String url = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.url.1");
        String userName = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.userName.1");
        String password = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.password.1");
        return getDruidDataSource(url, userName, password);
    }

    //https://github.com/alibaba/druid
    private DruidDataSource getDruidDataSource(String url, String userName, String password) {
        String driverClassName = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.driverClassName");
        //连接初始值,连接池启动时创建的连接数量的初始值
        String initialSize = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.initialSize");
        //最小空闲值,当空闲的连接数少于阀值时，连接池就会预申请去一些连接，以免洪峰来时来不及申请
        String minIdle = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.minIdle");
        //最大空闲值.当经过一个高峰时间后，连接池可以慢慢将已经用不到的连接慢慢释放一部分，一直减少到maxIdle为止 ，已经不再使用，配置了也没效果
        String maxIdle = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.maxIdle");
        //连接池的最大值,同一时间可以从池分配的最多连接数量
        String maxActive = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.maxActive");
        //获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
        String maxWaitMillis = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.maxWaitMillis");
        String useUnfairLock = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.useUnfairLock");
        //申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        String testOnBorrow = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.testOnBorrow");
        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        String testOnReturn = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.testOnReturn");
        //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        String testWhileIdle = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.testWhileIdle");
        /*有两个含义：
        1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。
        2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明*/
        String timeBetweenEvictionRunsMillis = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.timeBetweenEvictionRunsMillis");
        //连接保持空闲而不被驱逐的最小时间
        String minEvictableIdleTimeMillis = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.minEvictableIdleTimeMillis");
        //用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
        String validationQuery = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.validationQuery");

        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setInitialSize(Integer.parseInt(initialSize));
        dataSource.setMinIdle(Integer.parseInt(minIdle));
        dataSource.setMaxActive(Integer.parseInt(maxActive));
        dataSource.setMaxWait(Long.parseLong(maxWaitMillis));
        dataSource.setUseUnfairLock(Boolean.parseBoolean(useUnfairLock));
        dataSource.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
        dataSource.setTestOnReturn(Boolean.parseBoolean(testOnReturn));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));
        dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(timeBetweenEvictionRunsMillis));
        dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(minEvictableIdleTimeMillis));
        dataSource.setValidationQuery(validationQuery);
        return dataSource;
    }


    @Bean
    public DataSource dataSource() throws SQLException {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置第一个数据源
        DataSource dataSource1 = initOriginDataSource0();
        dataSourceMap.put("management_0", dataSource1);

        // 配置第二个数据源
        DataSource dataSource2 = initOriginDataSource1();
        dataSourceMap.put("management_1", dataSource2);

        // 配置management_system_user表规则
//        TableRuleConfiguration managementSystemUserTableRuleConfig = new TableRuleConfiguration();
//        orderTableRuleConfig.setLogicTable("t_order");
//        orderTableRuleConfig.setActualDataNodes("db0.t_order_0, db0.t_order_1, db1.t_order_0, db1.t_order_1");
//
//
//        ShardingRuleConfiguration managementSystemUserTableRuleConfig = new ShardingRuleConfiguration();
//        managementSystemUserTableRuleConfig.getTableRuleConfigs().setLogicTable("management_system_user");
//        managementSystemUserTableRuleConfig.setActualDataNodes("management_${0..1}.management_system_user_${0..4}");
//
//        // 配置分库策略
//        managementSystemUserTableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("mobile", "com.zydcompany.management.config.mybatis.sharding.CustomDataSourcePreciseShardingAlgorithm"));
//
//        // 配置分表策略
//        managementSystemUserTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("mobile", "com.zydcompany.management.config.mybatis.sharding.CustomTablePreciseShardingAlgorithm"));
//
//        // 配置分片规则
//        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
//        shardingRuleConfig.getTableRuleConfigs().add(managementSystemUserTableRuleConfig);

        // 省略配置其它表规则...

        // 获取数据源对象
        Properties properties = new Properties();
        properties.put(ShardingPropertiesConstant.SQL_SHOW.getKey(), "true");//显示sql
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new ConcurrentHashMap(), properties);

        return dataSource;
    }
}
