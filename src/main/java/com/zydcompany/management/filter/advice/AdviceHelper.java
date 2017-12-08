package com.zydcompany.management.filter.advice;

import com.google.common.base.Strings;
import com.zydcompany.management.common.constant.NumberConstant;
import com.zydcompany.management.common.constant.SymbolConstant;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import org.springframework.core.MethodParameter;

public class AdviceHelper {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    public static final Integer INPUT_LOG_MAX_STR_LENGTH = NumberConstant.FIVE_HUNDRED;
    public static final Integer OUTPUT_LOG_MAX_STR_LENGTH = NumberConstant.FIVE_HUNDRED;

    public static void logInput(Object body, MethodParameter parameter) {
        String className = parameter.getMethod().getDeclaringClass().getName();
        String methodName = parameter.getMethod().getName();
        String inputStr = getSubLogStr(FastJSONHelper.serialize(body), INPUT_LOG_MAX_STR_LENGTH);
        log.info(className + SymbolConstant.BLANK + methodName + SymbolConstant.BLANK + SymbolConstant.INPUT_FLAG + SymbolConstant.BLANK + SymbolConstant.EQUALITY_SIGN + inputStr);
    }


    public static void logOutput(Object body, MethodParameter parameter) {
        String className = parameter.getMethod().getDeclaringClass().getName();
        String methodName = parameter.getMethod().getName();
        String outputStr = getSubLogStr(FastJSONHelper.serialize(body), OUTPUT_LOG_MAX_STR_LENGTH);
        log.info(className + SymbolConstant.BLANK + methodName + SymbolConstant.BLANK + SymbolConstant.OUTPUT_FLAG + SymbolConstant.BLANK + SymbolConstant.EQUALITY_SIGN + outputStr);
    }

    private static String getSubLogStr(String logStr, Integer logMaxStrLength) {
        if (!Strings.isNullOrEmpty(logStr) && logStr.length() > logMaxStrLength) {
            logStr = logStr.substring(NumberConstant.ZERO, logMaxStrLength);
        }
        return logStr;
    }

}
