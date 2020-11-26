package com.github.yungyu16.spring.http.annotion;

import com.github.yungyu16.spring.http.HttpClientProxyFactory;
import com.github.yungyu16.spring.http.converter.DefaultRequestConverter;
import com.github.yungyu16.spring.http.converter.DefaultResponseConverter;
import com.github.yungyu16.spring.http.converter.RequestConverter;
import com.github.yungyu16.spring.http.converter.ResponseConverter;
import com.github.yungyu16.spring.stub.annotation.ProxyStub;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ProxyStub(factoryType = HttpClientProxyFactory.class)
public @interface HttpClient {
    /**
     * 接口baseUrl
     */
    String baseUrl() default "";

    Class<? extends RequestConverter> requestConverterClass() default DefaultRequestConverter.class;

    Class<? extends ResponseConverter> responseConverterClass() default DefaultResponseConverter.class;
}
