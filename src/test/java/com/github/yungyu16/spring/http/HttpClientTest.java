package com.github.yungyu16.spring.http;

import com.github.yungyu16.spring.http.stub.GithubClient;
import com.github.yungyu16.spring.http.stub.LoginForm;
import com.github.yungyu16.spring.http.stub.LoginVO;
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
        LoginForm form = new LoginForm();
        form.setAccount("15156684305");
        form.setSmsCode("123456");
        LoginVO body = githubClient.listRepos(form)
                .body();
        System.out.println(body);
    }
}
