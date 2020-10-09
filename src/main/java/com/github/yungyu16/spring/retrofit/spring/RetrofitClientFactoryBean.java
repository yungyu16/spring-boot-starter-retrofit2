package com.github.yungyu16.spring.retrofit.spring;

import com.github.yungyu16.spring.retrofit.DefaultOkHttpClientLoader;
import com.github.yungyu16.spring.retrofit.OkHttpClientLoader;
import com.github.yungyu16.spring.retrofit.annotion.RetrofitClient;
import com.github.yungyu16.spring.retrofit.annotion.RetrofitInterceptor;
import com.github.yungyu16.spring.retrofit.interceptor.RequestTimeoutInterceptor;
import com.github.yungyu16.spring.retrofit.interceptor.RequestTrackInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;

import java.util.Arrays;
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
        OkHttpClient baseHttpClient = HTTP_CLIENT_LOADER.getBaseHttpClient();
        OkHttpClient.Builder builder = baseHttpClient.newBuilder()
                .addInterceptor(new RequestTimeoutInterceptor())
                .addInterceptor(new RequestTrackInterceptor(applicationContext));
        applyRetrofitInterceptors(builder, clientInterface);
        retrofitBuilder.client(builder.build());
        String baseUrl = retrofitClient.baseUrl();
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = applicationContext.getEnvironment().resolveRequiredPlaceholders(baseUrl);
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            retrofitBuilder.baseUrl(baseUrl);
        }
        retrofitBuilder.addConverterFactory(new CompositeConverterFactory(applicationContext, retrofitClient.requestConverterClass(), retrofitClient.responseConverterClass()));
        log.info("开始构建Retrofit Stub。interface：" + clientInterface + " baseUrl：" + baseUrl);
        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(clientInterface);
    }

    private void applyRetrofitInterceptors(OkHttpClient.Builder builder, Class<?> retrofitClientClass) {
        Assert.notNull(builder, "OkHttpClient.Builder");
        Assert.notNull(retrofitClientClass, "retrofitClientClass");
        String[] interceptorNames = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(applicationContext, RetrofitInterceptor.class);
        Arrays.stream(interceptorNames)
                .map(it -> applicationContext.getBean(it))
                .filter(it -> checkIfApply(retrofitClientClass, it))
                .map(it -> ((Interceptor) it))
                .forEach(builder::addInterceptor);
    }

    @NotNull
    private boolean checkIfApply(Class<?> retrofitClientClass, Object interceptor) {
        Class<?> interceptorClazz = ClassUtils.getUserClass(interceptor);
        AnnotationAttributes mergedAnnotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(interceptorClazz, RetrofitInterceptor.class);
        RetrofitInterceptor annotation = AnnotationUtils.findAnnotation(interceptorClazz, RetrofitInterceptor.class);
        if (annotation == null) {
            return false;
        }
        Class<?>[] includeClasses = annotation.includeClasses();
        Class<?>[] excludeClasses = annotation.excludeClasses();
        boolean included = true;
        boolean excluded = false;
        if (!(includeClasses.length == 1 && includeClasses[0] == Object.class)) {
            included = Arrays.stream(includeClasses).anyMatch(it -> it == retrofitClientClass);
        }
        if (!(excludeClasses.length == 1 && includeClasses[0] == Object.class)) {
            excluded = Arrays.stream(excludeClasses).anyMatch(it -> it == retrofitClientClass);
        }
        return included && !excluded;
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
