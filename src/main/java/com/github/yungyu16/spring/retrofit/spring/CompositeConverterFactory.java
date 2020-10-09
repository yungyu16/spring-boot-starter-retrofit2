package com.github.yungyu16.spring.retrofit.spring;

import com.github.yungyu16.spring.retrofit.converter.DefaultRequestConverter;
import com.github.yungyu16.spring.retrofit.converter.DefaultResponseConverter;
import com.github.yungyu16.spring.retrofit.converter.RequestConverter;
import com.github.yungyu16.spring.retrofit.converter.ResponseConverter;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/25.
 */
public class CompositeConverterFactory extends Converter.Factory {
    private static RequestConverter defaultRequestConverter = new DefaultRequestConverter();
    @SuppressWarnings("all")
    private static ResponseConverter defaultResponseConverter = new DefaultResponseConverter();
    private final ApplicationContext applicationContext;
    private final Class<? extends RequestConverter> requestConverterClazz;
    private final Class<? extends ResponseConverter> responseConverterClazz;

    public CompositeConverterFactory(ApplicationContext applicationContext,
                                     Class<? extends RequestConverter> requestConverterClazz,
                                     Class<? extends ResponseConverter> responseConverterClazz) {
        Assert.notNull(applicationContext, "applicationContext");
        Assert.notNull(requestConverterClazz, "requestConverterClazz");
        Assert.notNull(responseConverterClazz, "responseConverterClazz");
        this.applicationContext = applicationContext;
        this.requestConverterClazz = requestConverterClazz;
        this.responseConverterClazz = responseConverterClazz;
    }

    @Nullable
    @Override
    @EverythingIsNonNull
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        checkGenericType(type);
        ResponseConverter converter;
        if (responseConverterClazz.equals(DefaultResponseConverter.class)) {
            converter = defaultResponseConverter;
        } else {
            converter = applicationContext.getBean(responseConverterClazz);
        }
        return new Converter<ResponseBody, Object>() {
            @Nullable
            @Override
            public Object convert(@NotNull ResponseBody value) throws IOException {
                return converter.fromResponseBody(value, type);
            }
        };
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        checkGenericType(type);
        RequestConverter converter;
        if (requestConverterClazz.equals(DefaultRequestConverter.class)) {
            converter = defaultRequestConverter;
        } else {
            converter = applicationContext.getBean(requestConverterClazz);
        }
        return new Converter<Object, RequestBody>() {
            @Nullable
            @Override
            public RequestBody convert(@NotNull Object value) throws IOException {
                return converter.toRequestBody(value, type);
            }
        };
    }

    private void checkGenericType(Type type) {
        if (type == null) {
            throw new IllegalStateException("对不起，泛型参数和返回值处理比较复杂，拜托想想办法不要使用泛型，谢谢合作");
        }
        if (!(type instanceof Class)) {
            throw new IllegalStateException("对不起，暂时没精力支持泛型参数，拜托请想想办法不要用泛型参数。谢谢合作");
        }
    }
}
