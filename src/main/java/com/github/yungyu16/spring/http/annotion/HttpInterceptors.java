package com.github.yungyu16.spring.http.annotion;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/10/9
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HttpInterceptors {
    HttpInterceptor[] value();
}
