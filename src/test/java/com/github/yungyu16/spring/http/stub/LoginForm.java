package com.github.yungyu16.spring.http.stub;

import lombok.Data;

import java.util.Map;

@Data
public class LoginForm {

    private String account;

    private String smsCode;

    private String captcha;

    private String password;

    private Integer loginMode;

    private String registerChannelCode;
    private String clickId;
    private String currentLink;
    private String smsTemplateCode;

    /**
     * 闪验扩展字段
     */
    private Map<String, String> loginAttributes;

    /**
     * 维信 - 设备id
     */
    private String vcreditId;
}
