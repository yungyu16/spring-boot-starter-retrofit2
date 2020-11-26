package com.github.yungyu16.spring.http;

import com.github.yungyu16.spring.http.annotion.HttpClient;
import com.github.yungyu16.spring.http.annotion.RetrofitInterceptor;
import com.github.yungyu16.spring.http.interceptor.RequestTimeoutInterceptor;
import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import com.github.yungyu16.spring.stub.proxy.StubProxyFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * CreatedDate: 2020/11/26
 * Author: songjialin
 */
@Slf4j
public class HttpClientProxyFactory implements StubProxyFactory, ApplicationContextAware {
    private static final OkHttpClient BASE_HTTP_CLIENT;

    static {
        ServiceLoader<OkHttpClientLoader> loaders = ServiceLoader.load(OkHttpClientLoader.class);
        Iterator<OkHttpClientLoader> iterator = loaders.iterator();
        OkHttpClientLoader httpClientLoader;
        if (iterator.hasNext()) {
            httpClientLoader = iterator.next();
        } else {
            httpClientLoader = new DefaultOkHttpClientLoader();
        }
        BASE_HTTP_CLIENT = httpClientLoader.getBaseHttpClient();
    }

    private ApplicationContext applicationContext;

    @Override
    public <T> T createProxy(Class<T> stubInterface, ProxyStub stubAnnotation) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        HttpClient httpClient = AnnotationUtils.getAnnotation(stubInterface, HttpClient.class);
        OkHttpClient.Builder builder = BASE_HTTP_CLIENT.newBuilder()
                .addInterceptor(new RequestTimeoutInterceptor());
        //.addInterceptor(new RequestTrackInterceptor(applicationContext));
        applyRetrofitInterceptors(builder, stubInterface);
        retrofitBuilder.client(builder.build());
        String baseUrl = httpClient.baseUrl();
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = applicationContext.getEnvironment().resolveRequiredPlaceholders(baseUrl);
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            retrofitBuilder.baseUrl(baseUrl);
        }
        retrofitBuilder.addConverterFactory(new CompositeConverterFactory(applicationContext, httpClient.requestConverterClass(), httpClient.responseConverterClass()));
        log.info("开始构建Retrofit Stub。interface：" + stubInterface + " baseUrl：" + baseUrl);
        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(stubInterface);
    }

    private void applyRetrofitInterceptors(OkHttpClient.Builder builder, Class<?> retrofitClientClass) {
        Assert.notNull(builder, "OkHttpClient.Builder");
        Assert.notNull(retrofitClientClass, "retrofitClientClass");
        String[] interceptorNames = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(applicationContext, RetrofitInterceptor.class);
        Arrays.stream(interceptorNames)
                .map(it -> applicationContext.getBean(it))
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
