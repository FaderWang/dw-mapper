package com.faderw.spring;

import com.faderw.annotation.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.mapper.code.Style;

import javax.sql.DataSource;

/**
 * @author FaderW
 */
@Slf4j
public class AutoConfiguredMapperScannerRegistrar
        implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final String DATASOURCE_SUFFIX = "DataSource";

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.info("start to register mybatis mapper");
        String[] scanPackages = (String[]) importingClassMetadata.getAnnotationAttributes("com.faderw.annotation.EnableMybatisConfig").get("scanPackages");
        String[] datasourceNames = ((DefaultListableBeanFactory)registry).getBeanNamesForType(DataSource.class);
        for (String datasource : datasourceNames) {
            if (datasource.endsWith(DATASOURCE_SUFFIX)) {
                doExtraRegister(getPrefix(datasource), scanPackages, registry);
            }
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

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private void doExtraRegister(String prefix, String[] scanPackages, BeanDefinitionRegistry registry) {
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

        log.debug("Searching for mappers annotated with @Mapper");

        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        scanner.getMapperHelper().getConfig().setStyle(Style.normal);
        scanner.setMapperProperties(this.environment);
        scanner.setPrefix(prefix);

        try {
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            scanner.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName);
            scanner.setAnnotationClass(Mapper.class);
            scanner.registerFilters();
            scanner.doScan(scanPackages);
        } catch (IllegalStateException ex) {
            log.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
        }
    }

    private String getPrefix(String name) {
        return name.replace(DATASOURCE_SUFFIX, "");
    }

}
