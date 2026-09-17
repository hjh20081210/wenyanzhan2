package com.wenyan.api.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenyan.common.constant.AuthConstant;
import com.wenyan.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

/** token鉴权拦截器 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String auth = request.getHeader(AuthConstant.HEADER_TOKEN);
        String token = null;
        if (auth != null && auth.startsWith(AuthConstant.TOKEN_PREFIX)) {
            token = auth.substring(AuthConstant.TOKEN_PREFIX.length());
        } else if (auth != null) {
            token = auth;
        }
        if (token == null || !JwtUtil.isValid(token)) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> body = new HashMap<>();
            body.put("code", 401);
            body.put("message", "未登录或Token失效");
            body.put("data", null);
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return false;
        }
        Long userId = JwtUtil.parseUserId(token);
        request.setAttribute(AuthConstant.USER_ID_KEY, userId);
        return true;
    }
}
