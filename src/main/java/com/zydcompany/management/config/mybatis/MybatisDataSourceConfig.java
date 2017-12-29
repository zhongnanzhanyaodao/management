package com.zydcompany.management.config.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.zydcompany.management.config.mybatis.sharding.CustomDataSourcePreciseShardingAlgorithm;
import com.zydcompany.management.config.mybatis.sharding.CustomSystemUserTablePreciseShardingAlgorithm;
import com.zydcompany.management.config.mybatis.sharding.CustomUserDetailSupplementTablePreciseShardingAlgorithm;
import com.zydcompany.management.config.mybatis.sharding.ShardingConstant;
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

    /**
     * 提供DataSource给MybatisBaseConfig使用
     * <p>
     * 注意:如果sql中不包含SHARDING_COLUMN,则会去遍历所有的库和表,因此强制所有sql必须带唯一键SHARDING_COLUMN,否则会扩大影响范围。
     * 采用分布式主键不会造成分表中的主键重复,能保证按主键操作数据的准确性,但是也会去遍历所有的库和表,性能下降.
     * 禁止用存在分布式冲突情况的字段作为唯一键,这样操作会扩大影响范围。
     *
     * @return
     * @throws SQLException
     */
    @Bean
    public DataSource dataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        //配置分片规则,可以多个
        shardingRuleConfig.getTableRuleConfigs().add(getSystemUserTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getUserDetailSupplementTableRuleConfiguration());
        //不配置全局分库分表策略,在特定的表中定制各自的分库分表策略
        //配置其它信息如：显示sql
        Properties properties = new Properties();
        properties.put(ShardingPropertiesConstant.SQL_SHOW.getKey(), ManagementPropertiesUtil.getManagementBasicPropertiesValue("sharding.sql.show"));
        //获取数据源对象,返回给MybatisBaseConfig使用
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new ConcurrentHashMap(), properties);
        return dataSource;
    }

    /**
     * 配置SystemUserTable分片规则
     *
     * @return
     */
    private TableRuleConfiguration getSystemUserTableRuleConfiguration() {
        TableRuleConfiguration systemUserTableRuleConfig = new TableRuleConfiguration();
        systemUserTableRuleConfig.setLogicTable(ShardingConstant.SYSTEM_USER_LOGICTABLE);
        systemUserTableRuleConfig.setActualDataNodes(ShardingConstant.SYSTEM_USER_ACTUALDATANODES);
        //配置SystemUserTable的分库策略,因为actualDataNodes配置了多个库，不配置分库策略,则多个库都会执行sql
        systemUserTableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration(ShardingConstant.DATA_BASE_SHARDING_COLUMN, CustomDataSourcePreciseShardingAlgorithm.class.getName()));
        //配置SystemUserTable的分表策略,因为actualDataNodes配置了多个表，不配置分表策略,则多个表都会执行sql
        systemUserTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(ShardingConstant.SYSTEM_USER_SHARDING_COLUMN, CustomSystemUserTablePreciseShardingAlgorithm.class.getName()));
        return systemUserTableRuleConfig;
    }

    /**
     * 配置UserDetailSupplement分片规则
     *
     * @return
     */
    private TableRuleConfiguration getUserDetailSupplementTableRuleConfiguration() {
        TableRuleConfiguration userDetailSupplementTableRuleConfig = new TableRuleConfiguration();
        userDetailSupplementTableRuleConfig.setLogicTable(ShardingConstant.USER_DETAIL_SUPPLEMENT_LOGICTABLE);
        //actualDataNodes指定数据源只有management_0,即不分库，不用再配置分库策略
        userDetailSupplementTableRuleConfig.setActualDataNodes(ShardingConstant.USER_DETAIL_SUPPLEMENT_ACTUALDATANODES);
        //配置UserDetailSupplement的分表策略
        userDetailSupplementTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(ShardingConstant.USER_DETAIL_SUPPLEMENT_COLUMN, CustomUserDetailSupplementTablePreciseShardingAlgorithm.class.getName()));
        return userDetailSupplementTableRuleConfig;
    }

    /**
     * 配置真实数据源
     *
     * @return
     */
    private Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        //配置第一个真实数据源
        result.put(ShardingConstant.DATA_BASE_ORIGIN_NAME_0, initOriginDataSource0());
        //配置第二个真实数据源
        result.put(ShardingConstant.DATA_BASE_ORIGIN_NAME_1, initOriginDataSource1());
        return result;
    }

    private DataSource initOriginDataSource0() {
        String url = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.url.0");
        String userName = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.userName.0");
        String password = ManagementPropertiesUtil.getDatasourcePropertiesValue("dataSource.jdbc.password.0");
        return getDruidDataSource(url, userName, password);
    }

    private DataSource initOriginDataSource1() {
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
}
