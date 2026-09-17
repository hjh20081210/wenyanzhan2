package com.wenyan.api.controller;

import com.wenyan.common.result.Result;
import com.wenyan.entity.vo.LoginVO;
import com.wenyan.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 登录认证：微信/QQ OAuth */
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/wechat")
    public Result<LoginVO> wechat(@RequestParam(required = false) String code) {
        return Result.success(authService.wechatLogin(code));
    }

    @GetMapping("/qq")
    public Result<LoginVO> qq(@RequestParam(required = false) String code) {
        return Result.success(authService.qqLogin(code));
    }
}
