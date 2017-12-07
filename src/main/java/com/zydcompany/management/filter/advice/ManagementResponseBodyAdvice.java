package com.zydcompany.management.filter.advice;

import com.google.common.base.Strings;
import com.zydcompany.management.common.constant.NumberConstant;
import com.zydcompany.management.common.constant.SymbolConstant;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ThreadLocalUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

@ControllerAdvice
public class ManagementResponseBodyAdvice implements ResponseBodyAdvice {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    public static final Integer INPUT_STR_MAX_LOG_LENGTH = NumberConstant.FIVE_HUNDRED;
    public static final Integer OUTPUT_STR_MAX_LOG_LENGTH = NumberConstant.FIVE_HUNDRED;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            if (request.getURI() == null) {
                return body;
            }
            String uri = request.getURI().toString();
            if (Strings.isNullOrEmpty(uri)) {
                return body;
            }

            Map<String, String[]> parameterMap = ((ServletServerHttpRequest) request).getServletRequest().getParameterMap();
            String inputStr = FastJSONHelper.serialize(parameterMap);
            if (!Strings.isNullOrEmpty(inputStr) && inputStr.length() > INPUT_STR_MAX_LOG_LENGTH) {
                inputStr = inputStr.substring(NumberConstant.ZERO, INPUT_STR_MAX_LOG_LENGTH);
            }
            String traceId = ThreadLocalUtil.getTraceId();
            traceId = Strings.isNullOrEmpty(traceId) ? SymbolConstant.EMPTY_STR : traceId;
            String startTime = traceId.substring(traceId.lastIndexOf(SymbolConstant.UNDERLINE) + NumberConstant.ONE);
            Long costTime = null;
            if (!Strings.isNullOrEmpty(startTime)) {
                costTime = System.currentTimeMillis() - Long.valueOf(startTime);
            }
            String outpuStr = FastJSONHelper.serialize(body);
            if (!Strings.isNullOrEmpty(outpuStr) && outpuStr.length() > OUTPUT_STR_MAX_LOG_LENGTH) {
                outpuStr = outpuStr.substring(NumberConstant.ZERO, OUTPUT_STR_MAX_LOG_LENGTH);
            }
            log.info("beforeBodyWrite uri={} input={} output={} costTime={}", uri, inputStr, outpuStr, costTime);
        } catch (Exception e) {
            log.error("beforeBodyWrite Exception", e);
        }

        return body;
    }

}