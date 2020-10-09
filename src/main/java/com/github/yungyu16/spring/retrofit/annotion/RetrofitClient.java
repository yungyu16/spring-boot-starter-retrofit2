package com.github.yungyu16.spring.retrofit.annotion;

import com.github.yungyu16.spring.retrofit.converter.DefaultRequestConverter;
import com.github.yungyu16.spring.retrofit.converter.DefaultResponseConverter;
import com.github.yungyu16.spring.retrofit.converter.RequestConverter;
import com.github.yungyu16.spring.retrofit.converter.ResponseConverter;
import okhttp3.Interceptor;

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

    Class<? extends RequestConverter> requestConverterClass() default DefaultRequestConverter.class;

    Class<? extends ResponseConverter> responseConverterClass() default DefaultResponseConverter.class;

    Class<? extends Interceptor>[] interceptorClasses() default Interceptor.class;
}
