package com.insurance.management.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Bypass CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Extract header
        String token = request.getHeader("X-Auth-Token");

        // Validate token existence and value
        if (token == null || (!token.equals("ADMIN") && !token.equals("AGENT"))) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized: Missing or invalid token");
            return false;
        }

        // Validate method authorization (AGENT cannot DELETE)
        if ("DELETE".equalsIgnoreCase(request.getMethod()) && "AGENT".equals(token)) {
            sendErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden: Agent role cannot perform DELETE operations");
            return false;
        }

        // Request authorized
        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonPayload = String.format(
            "{\"status\":%d,\"message\":\"%s\",\"timestamp\":%d}",
            status.value(), message, System.currentTimeMillis()
        );
        response.getWriter().write(jsonPayload);
    }
}
