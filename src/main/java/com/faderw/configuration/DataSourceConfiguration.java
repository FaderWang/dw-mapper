package com.faderw.configuration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author FaderW
 */
@Configuration
public class DataSourceConfiguration {

    @ConfigurationProperties(prefix = "spring.datasource.ceres.drds")
    @Bean("ceresDataSource")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }
}
