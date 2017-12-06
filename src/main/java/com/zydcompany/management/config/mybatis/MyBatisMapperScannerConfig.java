package com.zydcompany.management.config.mybatis;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis扫描接口
 */
@Configuration
//在MybatisConfig后执行
@AutoConfigureAfter(MybatisBaseConfig.class)
public class MybatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {

        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        //mapper接口目录
        mapperScannerConfigurer.setBasePackage("com.zydcompany.management.mapper");
        return mapperScannerConfigurer;
    }

}