package com.wenyan.common.constant;

/** 鉴权常量 */
public interface AuthConstant {
    String HEADER_TOKEN = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    String USER_ID_KEY = "userId";
    String ADMIN_USER_ID_KEY = "adminUserId";
    // 允许匿名访问的路径
    String[] ANON_URLS = {
        "/api/oauth/**",
        "/api/article/list",
        "/api/article/detail",
        "/api/article/recommend",
        "/api/question/list",
        "/api/word/list",
        "/api/word/detail",
        "/doc.html", "/webjars/**", "/swagger-ui/**", "/v3/api-docs/**"
    };
}
