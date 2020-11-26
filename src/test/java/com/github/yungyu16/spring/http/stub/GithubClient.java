package com.github.yungyu16.spring.http.stub;

import com.github.yungyu16.spring.http.annotion.HttpClient;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * CreatedDate: 2020/11/26
 * Author: songjialin
 */
@HttpClient(baseUrl = "https://api.github.com")
public interface GithubClient {
    @GET("users/{user}/repos")
    Response<String> listRepos(@Path("user") String user);

}
