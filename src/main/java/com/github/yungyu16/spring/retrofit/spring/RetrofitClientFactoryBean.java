package com.github.yungyu16.spring.retrofit.spring;

import com.github.yungyu16.spring.retrofit.DefaultOkHttpClientLoader;
import com.github.yungyu16.spring.retrofit.OkHttpClientLoader;
import com.github.yungyu16.spring.retrofit.annotion.RetrofitClient;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Slf4j
public class RetrofitClientFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {
    private static final OkHttpClientLoader HTTP_CLIENT_LOADER;

    static {
        ServiceLoader<OkHttpClientLoader> loaders = ServiceLoader.load(OkHttpClientLoader.class);
        Iterator<OkHttpClientLoader> iterator = loaders.iterator();
        if (iterator.hasNext()) {
            HTTP_CLIENT_LOADER = iterator.next();
        } else {
            HTTP_CLIENT_LOADER = new DefaultOkHttpClientLoader();
        }
    }

    private final Class<T> clientInterface;
    private RetrofitClient retrofitClient;
    private ApplicationContext applicationContext;

    public RetrofitClientFactoryBean(Class<T> clientInterface) {
        Assert.notNull(clientInterface, "没理由啊，这里不应该NPE啊，你回滚代码吧");
        this.clientInterface = clientInterface;
        RetrofitClient retrofitClient = AnnotationUtils.findAnnotation(clientInterface, RetrofitClient.class);
        if (retrofitClient == null) {
            throw new RuntimeException("解析RetrofitClient异常，请确认接口上是否添加注解，理论上这行代码应该没机会执行才对，除非有人修改了基础代码");
        }
        this.retrofitClient = retrofitClient;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.client(HTTP_CLIENT_LOADER.getBaseHttpClient());
        String baseUrl = retrofitClient.baseUrl();
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = applicationContext.getEnvironment().resolveRequiredPlaceholders(baseUrl);
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            retrofitBuilder.baseUrl(baseUrl);
        }
        retrofitBuilder.addConverterFactory(new CompositeConverterFactory(applicationContext, retrofitClient.requestConverterClazz(), retrofitClient.responseConverterClazz()));
        log.info("开始构建Retrofit Stub。interface：" + clientInterface + " baseUrl：" + baseUrl);
        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(clientInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return clientInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
