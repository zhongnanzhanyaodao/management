package com.zydcompany.management.filter;

import com.google.common.base.Strings;
import com.zydcompany.management.exception.message.BaseExceptionMsg;
import com.zydcompany.management.filter.proxy.DiscoveryRequestWrapper;
import com.zydcompany.management.filter.proxy.HttpProxyHelper;
import com.zydcompany.management.filter.proxy.ServiceUrlInfo;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class ManagementFilter implements Filter {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = new DiscoveryRequestWrapper((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cache-Control");
        //是否需要转发请求
        if (ManagementPropertiesUtil.getProxyUrlPropertiesValues().contains(request.getRequestURI())) {
            log.info("ManagementFilter doFilter proxy uri={}", request.getRequestURI());
            proxyRequest(request, response);
            return;
        }

        chain.doFilter(servletRequest, servletResponse);

    }


    /**
     * 转发请求
     */
    private void proxyRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String uri = getUri(request);
        log.info("ManagementFilter proxyRequest uri={}", uri);
        ServiceUrlInfo urlDto = parseServiceName(uri);
        log.info("ManagementFilter proxyRequest ServiceUrlInfo={}", FastJSONHelper.serialize(urlDto));

        //组装转发url
        String proxyUrl = buildProxyUrl(ManagementPropertiesUtil.getManagementBasicPropertiesValue("proxy.host"), urlDto);
        log.info("ManagementFilter proxyRequest proxyUrl={}", proxyUrl);
        HttpProxyHelper.proxyRequest(request, response, proxyUrl);
    }


    /**
     * 组装代理url
     *
     * @param tarHost
     * @param urlInfo
     * @return
     */
    private String buildProxyUrl(String tarHost, ServiceUrlInfo urlInfo) {
        if (Strings.isNullOrEmpty(tarHost)) return null;
        if (Objects.isNull(urlInfo)) return tarHost;
        return tarHost + "/" + urlInfo.getServiceName() + urlInfo.getServiceSuffixUrl();
    }

    private ServiceUrlInfo parseServiceName(String requestUri) {
        if (requestUri == null || requestUri.length() < 1) return new ServiceUrlInfo();
        // uri不为空且以'/'字符开头
        int secondIndex = requestUri.indexOf('/', 1);
        if (secondIndex == -1) return new ServiceUrlInfo(requestUri.substring(1), "");
        // 拆分uri，获取serviceName
        String serviceSuffixUrl = requestUri.substring(secondIndex);
        String serviceName = requestUri.substring(1, secondIndex);
        return new ServiceUrlInfo(serviceName, serviceSuffixUrl);
    }

    /**
     * 获取http请求uri，并为Get请求拼装参数
     */
    private String getUri(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        if (Strings.isNullOrEmpty(uri)) {
            throw new ServletException(BaseExceptionMsg.REQUEST_URI_NULL_ERROR_MSG);
        }
        String queryString = (request.getQueryString() == null ? "" : request.getQueryString());
        return "".equals(queryString) ? uri : uri + "?" + queryString;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
