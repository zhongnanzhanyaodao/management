package com.zydcompany.management.filter.advice;

import com.google.common.base.Strings;
import com.zydcompany.management.common.constant.NumberConstant;
import com.zydcompany.management.common.constant.SymbolConstant;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

@ControllerAdvice
public class ManagementRequestBodyAdvice implements RequestBodyAdvice {
    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    public static final Integer INPUT_STR_MAX_LOG_LENGTH = NumberConstant.FIVE_HUNDRED;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String className = parameter.getMethod().getDeclaringClass().getName();
        String methodName = parameter.getMethod().getName();
        String inputStr = FastJSONHelper.serialize(body);
        if (!Strings.isNullOrEmpty(inputStr) && inputStr.length() > INPUT_STR_MAX_LOG_LENGTH) {
            inputStr = inputStr.substring(NumberConstant.ZERO, INPUT_STR_MAX_LOG_LENGTH);
        }
        log.info(className + SymbolConstant.BLANK + methodName + SymbolConstant.BLANK + SymbolConstant.INPUT_FLAG + SymbolConstant.BLANK + SymbolConstant.EQUALITY_SIGN + inputStr);
        return body;
    }
}
