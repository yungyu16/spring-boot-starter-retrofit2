package com.github.yungyu16.spring.http.annotion;

import com.github.yungyu16.spring.http.converter.DefaultReplyBodyConverter;
import com.github.yungyu16.spring.http.converter.ReplyBodyConverter;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReplyConverterType {
    Class<? extends ReplyBodyConverter> value() default DefaultReplyBodyConverter.class;
}
