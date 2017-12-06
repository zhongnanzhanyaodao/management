package com.zydcompany.management.util;

public class ThreadLocalUtil {
    private static ThreadLocal<String> traceIdContext = new ThreadLocal<String>();

    public static String getTraceId() {
        return traceIdContext.get();
    }

    public static void setTraceId(String traceId) {
        traceIdContext.set(traceId);
    }

    public static void removeTraceId() {
        traceIdContext.remove();
    }
}
