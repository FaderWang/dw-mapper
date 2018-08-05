package com.faderw.spring;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by FaderW on 2018/8/5
 */

public class MapperAnnotationTypeFilter extends AnnotationTypeFilter{

    private String value;
    Class<? extends Annotation> annotationType;
    boolean considerMetaAnnotations;

    public MapperAnnotationTypeFilter(Class<? extends Annotation> annotationType, String value) {
        this(annotationType, false);
        this.annotationType = annotationType;
        this.value = value;
    }

    public MapperAnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations) {
        super(annotationType, considerMetaAnnotations);
    }

    public MapperAnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations, String value) {
        this(annotationType, considerMetaAnnotations);
        this.value = value;
    }

    @Override
    protected boolean matchSelf(MetadataReader metadataReader) {
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        Map<String, Object> map = annotationMetadata.getAnnotationAttributes(this.annotationType.getName());
        boolean match = map.get("value") != null && map.get("value").equals(value);
        boolean filter = annotationMetadata.hasAnnotation(this.annotationType.getName())
                || this.considerMetaAnnotations && annotationMetadata.hasMetaAnnotation(this.annotationType.getName());
        return match && filter;
    }
}
