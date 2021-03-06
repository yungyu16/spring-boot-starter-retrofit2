package com.github.yungyu16.spring.http.stub;

import com.github.yungyu16.spring.http.annotion.HttpClient;
import com.github.yungyu16.spring.http.annotion.HttpInterceptor;
import com.github.yungyu16.spring.http.annotion.ReplyConverterType;
import com.github.yungyu16.spring.http.annotion.ReqConverterType;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * CreatedDate: 2020/11/26
 * Author: songjialin
 */
@HttpClient("https://api.dev.50lion.com/web-app")
@ReqConverterType(GithubReqBodyConverter.class)
@ReplyConverterType(GithubReplyBodyConverter.class)
@HttpInterceptor(TestInterceptor1.class)
@HttpInterceptor(TestInterceptor2.class)
public interface GithubClient {

    @POST("account/login")
        // @ReqConverterType(GithubReqBodyConverter.class)
        // @ReplyConverterType(GithubReplyBodyConverter.class)
    Response<LoginVO> listRepos(@Body LoginForm form);
}
