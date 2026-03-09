package com.example.ProjectManagementSystem.exception;

import com.example.ProjectManagementSystem.config.SkipResponseWrap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
@RestControllerAdvice(
        basePackages = "com.example.ProjectManagementSystem.controller"
)
@RequiredArgsConstructor
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return returnType.getContainingClass()
                .getPackageName()
                .startsWith("com.example.ProjectManagementSystem.controller");
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body instanceof ApiResponse || body instanceof ApiErrorResponse) {
            return body;
        }

        HttpStatus status = HttpStatus.OK;

        if(response instanceof ServletServerHttpResponse servletResponse){
            status = HttpStatus.valueOf(servletResponse.getServletResponse().getStatus());
        }

        ApiResponse<Object> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                status.value(),
                "Success",
                body
        );

        if(body instanceof String){
            try {
                return objectMapper.writeValueAsString(apiResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return apiResponse;
    }
}