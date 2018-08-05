package com.faderw.annotation;

import java.lang.annotation.*;

/**
 * @author FaderW
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface Mapper {
    String value();
}
