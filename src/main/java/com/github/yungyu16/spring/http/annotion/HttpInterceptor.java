package com.github.yungyu16.spring.http.annotion;

import okhttp3.Interceptor;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/10/9
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(HttpInterceptors.class)
public @interface HttpInterceptor {
    Class<? extends Interceptor> value() default Interceptor.class;

    /**
     * 从小到大升序排列组成拦截器链
     *
     * @return
     */
    int index() default -1;
}
