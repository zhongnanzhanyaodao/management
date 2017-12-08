package com.zydcompany.management.filter.advice;

import com.zydcompany.management.util.ManagementLogUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ManagementResponseBodyAdvice implements ResponseBodyAdvice {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            AdviceHelper.logOutput(body, returnType);
        } catch (Exception e) {
            log.error("beforeBodyWrite Exception", e);
        }

        return body;
    }

}