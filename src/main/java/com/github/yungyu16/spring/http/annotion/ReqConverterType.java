package com.github.yungyu16.spring.http.annotion;

import com.github.yungyu16.spring.http.converter.DefaultReqBodyConverter;
import com.github.yungyu16.spring.http.converter.ReqBodyConverter;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReqConverterType {
    Class<? extends ReqBodyConverter> value() default DefaultReqBodyConverter.class;
}
