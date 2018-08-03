package com.faderw.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author  FaderW
 * 2018/8/3
 */
@Slf4j
@Configuration
@Import(MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class)
public class MybatisAutoConfiguration {


    /**
     * This will just scan the same base package as Spring Boot does. If you want
     * more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
     * repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
            implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private static final String DATASOURCE_SUFFIX = "DataSource";

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            log.info("start to register mybatis mapper");
            String[] datasourceNames = ((DefaultListableBeanFactory)registry).getBeanNamesForType(DataSource.class);
            for (String datasource : datasourceNames) {
                if (datasource.endsWith(DATASOURCE_SUFFIX)) {
                    doExtraRegister(getPrefix(datasource), registry);
                }
            }


            log.debug("Searching for mappers annotated with @Mapper");

            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            try {
                if (this.resourceLoader != null) {
                    scanner.setResourceLoader(this.resourceLoader);
                }
                scanner.setAnnotationClass(Mapper.class);
                scanner.registerFilters();
                scanner.doScan("com.faderw");
            } catch (IllegalStateException ex) {
                log.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
            }
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        private void doExtraRegister(String prefix, BeanDefinitionRegistry registry) {
            String dataSourceBeanName = prefix + "DataSource";
            String sqlSessionFactoryBeanName = prefix + "SqlSessionFactory";
            String sqlSessionTemplateBeanName = prefix + "SqlSessionTemplate";
            String transactionManagerBeanName = prefix + "TransactionManager";

            BeanDefinitionBuilder sqlSessionFactory = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBean.class);
            sqlSessionFactory.addPropertyReference("datasource", dataSourceBeanName);
            registry.registerBeanDefinition(sqlSessionFactoryBeanName, sqlSessionFactory.getBeanDefinition());

            BeanDefinitionBuilder sqlSessionTemplate = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class);
            sqlSessionTemplate.addConstructorArgReference(sqlSessionTemplateBeanName);
            registry.registerBeanDefinition(sqlSessionTemplateBeanName, sqlSessionTemplate.getBeanDefinition());

            BeanDefinitionBuilder transactionManager = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
            transactionManager.addConstructorArgReference(dataSourceBeanName);
            registry.registerBeanDefinition(transactionManagerBeanName, transactionManager.getBeanDefinition());
        }

        private String getPrefix(String name) {
            return name.replace(DATASOURCE_SUFFIX, "");
        }
    }

}
