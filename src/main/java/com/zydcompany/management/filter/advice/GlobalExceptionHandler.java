package com.zydcompany.management.filter.advice;

import com.zydcompany.management.common.PlatformResponse;
import com.zydcompany.management.exception.BusinessException;
import com.zydcompany.management.exception.message.BaseExceptionMsg;
import com.zydcompany.management.util.ManagementLogUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕捉处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static ManagementLogUtil log = ManagementLogUtil.getLogger();

    @ExceptionHandler(value = Exception.class)
    public PlatformResponse baseExceptionHandler(Exception e) {
        log.error("GlobalExceptionHandler baseExceptionHandler Exception", e);
        return PlatformResponse.builder().code(BaseExceptionMsg.FAIL_CODE).msg(BaseExceptionMsg.FAIL_MSG).build();
    }

    /**
     * 拦截捕捉自定义异常BusinessException
     */
    @ExceptionHandler(value = BusinessException.class)
    public PlatformResponse businessExceptionHandler(BusinessException e) {
        return PlatformResponse.builder().code(e.getCode()).msg(e.getMsg()).build();
    }

}
