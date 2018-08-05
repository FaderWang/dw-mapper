package com.faderw.annotation;

import com.faderw.spring.AutoConfiguredMapperScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author FaderW
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AutoConfiguredMapperScannerRegistrar.class)
public @interface EnableMybatisConfig {
    String[] scanPackages();
}
