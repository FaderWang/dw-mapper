package com.faderw;

import com.faderw.annotation.EnableMybatisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableMybatisConfig(scanPackages = {"com.faderw.mapper"})
@PropertySource(value = {"classpath:application.properties",
"classpath:application-db-config.properties"})
public class DwMapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(DwMapperApplication.class, args);
	}
}
