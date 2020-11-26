package com.github.yungyu16.spring.http;

import com.github.yungyu16.spring.http.annotion.ReplyConverterType;
import com.github.yungyu16.spring.http.annotion.ReqConverterType;
import com.github.yungyu16.spring.http.converter.DefaultReplyBodyConverter;
import com.github.yungyu16.spring.http.converter.DefaultReqBodyConverter;
import com.github.yungyu16.spring.http.converter.ReplyBodyConverter;
import com.github.yungyu16.spring.http.converter.ReqBodyConverter;
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
import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/25.
 */
public class CompositeConverterFactory extends Converter.Factory {
    private static ReqBodyConverter defaultReqBodyConverter = new DefaultReqBodyConverter();
    @SuppressWarnings("all")
    private static ReplyBodyConverter defaultReplyBodyConverter = new DefaultReplyBodyConverter();
    private final ApplicationContext applicationContext;
    private final ReqConverterType reqConverterType;
    private final ReplyConverterType replyConverterType;

    public CompositeConverterFactory(ApplicationContext applicationContext, ReqConverterType reqConverterType, ReplyConverterType replyConverterType) {
        Assert.notNull(applicationContext, "applicationContext");
        Assert.notNull(reqConverterType, "reqConverterType");
        Assert.notNull(replyConverterType, "replyConverterType");
        this.applicationContext = applicationContext;
        this.reqConverterType = reqConverterType;
        this.replyConverterType = replyConverterType;
    }

    @Nullable
    @Override
    @EverythingIsNonNull
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Class<? extends ReplyBodyConverter> converterType = getReplyConverterType(annotations)
                .orElse(replyConverterType)
                .value();
        ReplyBodyConverter converter;
        if (converterType.equals(DefaultReplyBodyConverter.class)) {
            converter = defaultReplyBodyConverter;
        } else {
            converter = applicationContext.getBean(converterType);
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
        Class<? extends ReqBodyConverter> converterType = getReqConverterType(methodAnnotations)
                .orElse(reqConverterType)
                .value();
        ReqBodyConverter converter;
        if (converterType.equals(DefaultReqBodyConverter.class)) {
            converter = defaultReqBodyConverter;
        } else {
            converter = applicationContext.getBean(converterType);
        }
        return new Converter<Object, RequestBody>() {
            @Nullable
            @Override
            public RequestBody convert(@NotNull Object value) throws IOException {
                return converter.toRequestBody(value, type);
            }
        };
    }

    private Optional<ReqConverterType> getReqConverterType(Annotation[] annotations) {
        return getSpecificAnnotation(annotations, ReqConverterType.class);
    }

    private Optional<ReplyConverterType> getReplyConverterType(Annotation[] annotations) {
        return getSpecificAnnotation(annotations, ReplyConverterType.class);
    }

    private <T extends Annotation> Optional<T> getSpecificAnnotation(Annotation[] annotations, Class<T> target) {
        if (target == null) {
            return Optional.empty();
        }
        if (annotations == null) {
            return Optional.empty();
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == target) {
                return Optional.of((T) annotation);
            }
        }
        return Optional.empty();
    }
}
