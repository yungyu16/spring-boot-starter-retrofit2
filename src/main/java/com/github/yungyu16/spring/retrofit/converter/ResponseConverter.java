package com.github.yungyu16.spring.retrofit.converter;

import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
public interface ResponseConverter {
    Object fromResponseBody(@NotNull ResponseBody body, Type type);
}
