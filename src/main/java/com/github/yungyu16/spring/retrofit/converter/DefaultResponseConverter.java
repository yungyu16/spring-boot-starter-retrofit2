package com.github.yungyu16.spring.retrofit.converter;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.spring.retrofit.error.RetrofitException;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
public class DefaultResponseConverter implements ResponseConverter {
    // private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object fromResponseBody(@NotNull ResponseBody body, Type type) {
        try {
            byte[] bytes = body.bytes();
            return JSON.parseObject(bytes, type);
        } catch (IOException e) {
            throw new RetrofitException("网络错误", e);
        }
    }
}
