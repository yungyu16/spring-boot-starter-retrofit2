package com.github.yungyu16.spring.retrofit.annotion;

import com.github.yungyu16.spring.retrofit.spring.RetrofitBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RetrofitBeanDefinitionRegistrar.class)
public @interface EnableRetrofitClient {
    Class<?>[] basePackageClasses() default {};

    String[] basePackages() default {};
}
