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
public @interface RetrofitInterceptor {
    /**
     * 包含的存根接口列表
     *
     * @return
     */
    Class<?>[] includeClasses() default Object.class;

    /**
     * 排除的存根接口列表
     *
     * @return
     */
    Class<?>[] excludeClasses() default Object.class;
}
