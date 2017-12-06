package com.zydcompany.management.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagementLogUtil {

    private Logger log;
    private static final String TAB_BLANK = "  ";
    private static final String BLANK = " ";
    private static final String EMPTY_STR = "";
    private static final String DOT = ".";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET = ")";
    private static final String HORIZONTAL_TRUNK = "-";
    private static final String COLON = ":";
    private static final String TRACEID_PREFIX = "traceId=";
    private static final String JAVA = "java";
    private static final Integer CALL_LOG_STACKTRACE_ELEMENT_INDEX = 3;

    private static class SingletonHolder {
        private static ManagementLogUtil managementLogUtil = new ManagementLogUtil();
    }

    public static ManagementLogUtil getLogger() {
        return SingletonHolder.managementLogUtil;
    }


    private ManagementLogUtil() {
        log = LoggerFactory.getLogger(ManagementLogUtil.class);
    }

    public void info(String info) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.info(infoFormat);
    }

    public void info(String info, Object args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.info(infoFormat, args);
    }

    public void info(String info, Object... args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.info(infoFormat, args);
    }

    public void info(String info, Throwable throwable) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.info(infoFormat, throwable);
    }

    public void debug(String info) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.debug(infoFormat);
    }

    public void debug(String info, Object args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.debug(infoFormat, args);
    }

    public void debug(String info, Object... args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.debug(infoFormat, args);
    }

    public void debug(String info, Throwable throwable) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.debug(infoFormat, throwable);
    }

    public void error(String info) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.error(infoFormat);
    }

    public void error(String info, Object args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.error(infoFormat, args);
    }

    public void error(String info, Object... args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.error(infoFormat, args);
    }

    public void error(String info, Throwable throwable) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.error(infoFormat, throwable);
    }

    public void warn(String info) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.warn(infoFormat);
    }

    public void warn(String info, Object args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.warn(infoFormat, args);
    }

    public void warn(String info, Object... args) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.warn(infoFormat, args);
    }

    public void warn(String info, Throwable throwable) {
        String infoFormat = generatorPrixMsg() + TRACEID_PREFIX + ThreadLocalUtil.getTraceId() + TAB_BLANK + info;
        log.warn(infoFormat, throwable);
    }

    private static String generatorPrixMsg() {
        final StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        String msg = EMPTY_STR;
        if ((stackTraceElement.length > CALL_LOG_STACKTRACE_ELEMENT_INDEX) && (stackTraceElement[CALL_LOG_STACKTRACE_ELEMENT_INDEX] != null)) {
            final StackTraceElement callLogStackTraceElement = stackTraceElement[CALL_LOG_STACKTRACE_ELEMENT_INDEX];
            final String clazzNameFull = callLogStackTraceElement.getClassName();
            final String methodName = callLogStackTraceElement.getMethodName();
            final String clazzName = clazzNameFull.substring(clazzNameFull.lastIndexOf(DOT) + 1, clazzNameFull.length());
            final Integer lineNumber = callLogStackTraceElement.getLineNumber();
            msg = clazzNameFull + DOT + methodName + LEFT_BRACKET + clazzName + DOT + JAVA + COLON + lineNumber + RIGHT_BRACKET + BLANK + HORIZONTAL_TRUNK + BLANK;
        }
        return msg;
    }

}
