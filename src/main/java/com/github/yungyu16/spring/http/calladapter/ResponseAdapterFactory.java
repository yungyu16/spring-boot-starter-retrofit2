package com.github.yungyu16.spring.http.calladapter;

import com.github.yungyu16.spring.http.error.RetrofitException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ResponseAdapterFactory extends CallAdapter.Factory {

    @Override
    public @Nullable
    CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Response.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Response return type must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        return new CallAdapter<Object, Response<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public Response<?> adapt(Call<Object> call) {
                try {
                    return call.execute();
                } catch (IOException e) {
                    String url = call.request().url().toString();
                    throw new RetrofitException("IOException:" + url, e);
                }
            }
        };
    }
}
