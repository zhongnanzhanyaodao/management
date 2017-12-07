package com.zydcompany.management.filter.advice;

import com.google.common.base.Strings;
import com.zydcompany.management.common.constant.NumberConstant;
import com.zydcompany.management.util.FastJSONHelper;
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

    public static final Integer OUTPUT_STR_MAX_LOG_LENGTH = NumberConstant.FIVE_HUNDRED;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            String outpuStr = FastJSONHelper.serialize(body);
            if (!Strings.isNullOrEmpty(outpuStr) && outpuStr.length() > OUTPUT_STR_MAX_LOG_LENGTH) {
                outpuStr = outpuStr.substring(NumberConstant.ZERO, OUTPUT_STR_MAX_LOG_LENGTH);
            }
            log.info("beforeBodyWrite output={}", outpuStr);
        } catch (Exception e) {
            log.error("beforeBodyWrite Exception", e);
        }

        return body;
    }

}