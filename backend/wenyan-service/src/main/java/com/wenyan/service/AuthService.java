package com.wenyan.service;

import com.wenyan.entity.vo.LoginVO;

/** 登录认证服务 */
public interface AuthService {
    /** 微信OAuth登录 */
    LoginVO wechatLogin(String code);
    /** QQ OAuth登录 */
    LoginVO qqLogin(String code);
}
