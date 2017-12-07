package com.zydcompany.management.filter.interceptor;

import com.google.common.base.Strings;
import com.zydcompany.management.common.constant.NumberConstant;
import com.zydcompany.management.common.constant.SymbolConstant;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


public class ManagementInterceptor implements HandlerInterceptor {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    private static final String NO_STATISTICS = "no statistics";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = UUID.randomUUID().toString() + SymbolConstant.UNDERLINE + System.currentTimeMillis();
        ThreadLocalUtil.setTraceId(uuid);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String traceId = ThreadLocalUtil.getTraceId();
        traceId = Strings.isNullOrEmpty(traceId) ? SymbolConstant.EMPTY_STR : traceId;
        String startTime = traceId.substring(traceId.lastIndexOf(SymbolConstant.UNDERLINE) + NumberConstant.ONE);
        String costTime = Strings.isNullOrEmpty(startTime) ? NO_STATISTICS : String.valueOf(System.currentTimeMillis() - Long.valueOf(startTime));
        log.info("url={} costTime={}", request.getRequestURL(), costTime);
        ThreadLocalUtil.removeTraceId();
    }
}
