package com.zydcompany.management.filter;

import com.google.common.base.Strings;
import com.zydcompany.management.filter.proxy.DiscoveryRequestWrapper;
import com.zydcompany.management.filter.proxy.HttpProxyHelper;
import com.zydcompany.management.filter.proxy.ServiceUrlInfo;
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

        //拦截器设置
        HttpServletRequest request = new DiscoveryRequestWrapper((HttpServletRequest) servletRequest);
        log.info("ManagementFilter doFilter request={}", request);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cache-Control");

        //转发请求
        proxyRequest(request, response);
    }


    /**
     * 转发请求道Server端
     */
    private void proxyRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        //根据url匹配服务实例的名称
        String uri = getUri(request);
        log.info("aDiscoveryFilter requestUri: " + uri);
        ServiceUrlInfo urlDto = parseServiceName(uri);
        log.info("aDiscoveryFilter ServiceUrlInfo: " + urlDto);

        //获取转发url
        String proxyUrl = buildProxyUrl(ManagementPropertiesUtil.getManagementBasicPropertiesValue("proxy.host"), urlDto);
        log.info("aDiscoveryFilter proxyUrl: {}", proxyUrl);
        HttpProxyHelper.proxyRequest(request, response, proxyUrl);
    }


    /**
     * 获取代理url
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

    /**
     * 解析ServiceName，该Name用于发现启动的服务
     */
    private ServiceUrlInfo parseServiceName(String requestUri) {
        if (requestUri == null || requestUri.length() < 1) return new ServiceUrlInfo();
        // uri不为空且以'/'字符开头
        int secondIndex = requestUri.indexOf('/', 1);
        if (secondIndex == -1) return new ServiceUrlInfo(requestUri.substring(1), "");
        // 拆分uri，获取serviceName
        String serviceUrl = requestUri.substring(secondIndex);
        String serviceName = requestUri.substring(1, secondIndex);
        return new ServiceUrlInfo(serviceName, serviceUrl);
    }

    /**
     * 获取http请求uri，并为Get请求拼装参数
     */
    private String getUri(HttpServletRequest request) throws ServletException {
        // 获取客户端请求的uri
        String uri = request.getRequestURI();
        if (Strings.isNullOrEmpty(uri)) {
            throw new ServletException("web层转发器处理异常，uri为空，无有效的访问资源");
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
