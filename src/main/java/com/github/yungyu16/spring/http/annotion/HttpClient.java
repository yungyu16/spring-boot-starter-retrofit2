package com.github.yungyu16.spring.http.annotion;

import com.github.yungyu16.spring.http.HttpClientProxyFactory;
import com.github.yungyu16.spring.http.converter.DefaultReplyBodyConverter;
import com.github.yungyu16.spring.http.converter.DefaultReqBodyConverter;
import com.github.yungyu16.spring.http.converter.ReplyBodyConverter;
import com.github.yungyu16.spring.http.converter.ReqBodyConverter;
import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ProxyStub(factoryType = HttpClientProxyFactory.class)
@ReplyConverterType
@ReqConverterType
public @interface HttpClient {
    String baseUrl() default "";

    @AliasFor(annotation = ReqConverterType.class, attribute = "value")
    Class<? extends ReqBodyConverter> ReplyConverterType() default DefaultReqBodyConverter.class;

    @AliasFor(annotation = ReplyConverterType.class, attribute = "value")
    Class<? extends ReplyBodyConverter> ReqConverterType() default DefaultReplyBodyConverter.class;
}
