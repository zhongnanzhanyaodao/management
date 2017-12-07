package com.zydcompany.management.exception;

import com.zydcompany.management.exception.message.BaseExceptionMsg;
import com.zydcompany.management.util.ManagementLogUtil;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    private Integer code = BaseExceptionMsg.SUCCESS_CODE;
    private String msg = BaseExceptionMsg.SUCCESS_MSG;


    private BusinessException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BusinessException createBusinessException(Integer code, String msg) {
        String callChainStr = ManagementLogUtil.generatorPrixMsg();
        log.error("{} businessException code={} msg={}", callChainStr, code, msg);
        BusinessException businessException = new BusinessException(code, msg);
        return businessException;
    }

    public static BusinessException createBusinessException(String msg) {
        String callChainStr = ManagementLogUtil.generatorPrixMsg();
        log.error("{} businessException code={} msg={}", callChainStr, BaseExceptionMsg.PRECONDITIONS_FAIL_CODE, msg);
        BusinessException businessException = new BusinessException(BaseExceptionMsg.PRECONDITIONS_FAIL_CODE, msg);
        return businessException;
    }


}
