package com.github.yungyu16.spring.retrofit.annotion;

import com.github.yungyu16.spring.retrofit.converter.DefaultRequestConverter;
import com.github.yungyu16.spring.retrofit.converter.DefaultResponseConverter;
import com.github.yungyu16.spring.retrofit.converter.RequestConverter;
import com.github.yungyu16.spring.retrofit.converter.ResponseConverter;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetrofitClient {
    /**
     * 接口baseUrl
     *
     * @return
     */
    String baseUrl() default "";

    Class<? extends RequestConverter> requestConverterClazz() default DefaultRequestConverter.class;

    Class<? extends ResponseConverter> responseConverterClazz() default DefaultResponseConverter.class;
}
