package com.github.yungyu16.spring.http;

import com.github.yungyu16.spring.http.annotion.HttpClient;
import com.github.yungyu16.spring.http.annotion.ReplyConverterType;
import com.github.yungyu16.spring.http.annotion.ReqConverterType;
import com.github.yungyu16.spring.http.annotion.RetrofitInterceptor;
import com.github.yungyu16.spring.http.calladapter.ResponseAdapterFactory;
import com.github.yungyu16.spring.http.interceptor.RequestTimeoutInterceptor;
import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import com.github.yungyu16.spring.stub.proxy.StubProxyFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
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
    private static final ResponseAdapterFactory RESPONSE_ADAPTER_FACTORY = new ResponseAdapterFactory();
    private static final OkHttpClient BASE_HTTP_CLIENT;
    private ApplicationContext applicationContext;

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

    @Override
    public <T> T createProxy(Class<T> stubInterface, ProxyStub stubAnnotation) {
        HttpClient httpClient = AnnotationUtils.getAnnotation(stubInterface, HttpClient.class);
        ReqConverterType reqConverterType = AnnotationUtils.getAnnotation(stubInterface, ReqConverterType.class);
        ReplyConverterType replyConverterType = AnnotationUtils.getAnnotation(stubInterface, ReplyConverterType.class);
        if (httpClient == null) {
            throw new NullPointerException(stubInterface.getName() + "上没有@httpClient注解");
        }
        Call.Factory callFactory = buildCallFactory(stubInterface);
        String baseUrl = buildBaseUrl(httpClient);
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory(callFactory)
                .addCallAdapterFactory(RESPONSE_ADAPTER_FACTORY)
                .addConverterFactory(new CompositeConverterFactory(applicationContext, reqConverterType, replyConverterType));
        log.debug("new Retrofit HttpClient.interface：" + stubInterface + " baseUrl：" + baseUrl);
        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(stubInterface);
    }

    @NotNull
    private String buildBaseUrl(HttpClient httpClient) {
        String baseUrl = httpClient.baseUrl();
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = baseUrl.trim();
            baseUrl = applicationContext.getEnvironment().resolveRequiredPlaceholders(baseUrl);
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
        }
        return baseUrl;
    }

    @NotNull
    private <T> Call.Factory buildCallFactory(Class<T> stubInterface) {
        OkHttpClient.Builder builder = BASE_HTTP_CLIENT.newBuilder()
                .addInterceptor(new RequestTimeoutInterceptor());
        //.addInterceptor(new RequestTrackInterceptor(applicationContext));
        applyRetrofitInterceptors(builder, stubInterface);
        return builder.build();
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
