package com.wenyan.entity.vo;

import lombok.Data;

import java.io.Serializable;

/** 登录返回 */
@Data
public class LoginVO implements Serializable {
    private String token;
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer isFirstInit; // 1新用户需走初始化流程 0老用户
}
