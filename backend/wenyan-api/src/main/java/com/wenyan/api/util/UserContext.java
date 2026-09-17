package com.wenyan.api.util;

import com.wenyan.common.constant.AuthConstant;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/** 当前登录用户上下文 */
public final class UserContext {
    private UserContext() {}

    public static Long getUserId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest request = attrs.getRequest();
        Object id = request.getAttribute(AuthConstant.USER_ID_KEY);
        return id == null ? null : (Long) id;
    }
}
