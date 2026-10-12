package com.secondhand.interceptor;

import com.secondhand.common.JwtUtil;
import com.secondhand.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录鉴权拦截器：校验请求头 Authorization: Bearer <token>
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Long userId = JwtUtil.getUserId(token);
            if (userId != null) {
                request.setAttribute("userId", userId);
                request.setAttribute("role", JwtUtil.getRole(token));
                return true;
            }
        }
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(MAPPER.writeValueAsString(Result.error(401, "未登录或登录已过期，请重新登录")));
        return false;
    }
}
