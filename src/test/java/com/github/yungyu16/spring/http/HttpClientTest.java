package com.github.yungyu16.spring.http;

import com.github.yungyu16.spring.http.stub.GithubClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * CreatedDate: 2020/11/25
 * Author: songjialin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpClientTest {
    @Autowired
    private GithubClient githubClient;

    @Test
    public void test() throws IOException {
        String body = githubClient.listRepos("yungyu16")
                .execute()
                .body();
        System.out.println(body);
    }
}
