package com.logisync.sistema_cadena_suministro.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Renders 401/403 raised by the filter chain with the same problem+json shape as the global handler. */
@Component
public class RestSecurityProblemHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        write(response, HttpStatus.UNAUTHORIZED, "unauthorized", "Authentication is required or the token is invalid");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        write(response, HttpStatus.FORBIDDEN, "forbidden", "You do not have permission to perform this action");
    }

    private void write(HttpServletResponse response, HttpStatus status, String code, String detail)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"type\":\"urn:logisync:problem:" + code + "\",\"title\":\""
                + status.getReasonPhrase() + "\",\"status\":" + status.value() + ",\"detail\":\"" + detail + "\"}");
    }
}
