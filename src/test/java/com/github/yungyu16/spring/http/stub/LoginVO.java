package com.github.yungyu16.spring.http.stub;

import lombok.Data;

@Data
public class LoginVO {
    private String code;
    private String msg;
    private String nextAction = "NONE";
    private String accessToken;
    private String accountMask;
    private String account;

    public static LoginVO of(String nextAction) {
        LoginVO loginVO = new LoginVO();
        loginVO.setNextAction(nextAction);
        return loginVO;
    }
}
