package com.zydcompany.management.filter.proxy;


import com.google.common.base.Strings;
import com.zydcompany.management.exception.message.BaseExceptionMsg;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpProxyHelper {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    /**
     * url是否可以通过，不可以通过直接返回
     *
     * @param request
     * @param response
     * @return
     */
    private static boolean urlPass(HttpServletRequest request, HttpServletResponse response) {
        try {
            String url = request.getRequestURI();
            if (Strings.isNullOrEmpty(url)) return false;
            if (ManagementPropertiesUtil.getOpenUrlPropertiesValues().contains(url)) {
                log.info("HttpProxyHelper uriPass input url={}", url);
                HttpProxyHelper.handleHttpErrorResponse(response, HttpStatus.SC_UNAUTHORIZED, request.getRequestURI(),
                        BaseExceptionMsg.OPEN_URL_ERROR_MSG, BaseExceptionMsg.OPEN_URL_ERROR_MSG, BaseExceptionMsg.OPEN_URL_ERROR_MSG);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("HttpProxyUtil urlPass error targetUrl={} error={}", request.getRequestURI(), e.getMessage());
        }
        return false;
    }

    /**
     * 使用java原生HttpURLConnection转发请求, 调用方需保证参数正确性
     *
     * @param request   HttpRequest
     * @param response  HttpResponse
     * @param targetUrl 目标地址Url
     */
    public static void proxyRequest(HttpServletRequest request, HttpServletResponse response, String targetUrl) {
        log.info("HttpProxyHelper targetUrl={}", targetUrl);
        // url是否可以通过
        if (!urlPass(request, response)) return;


        // 拼装url和参数
        targetUrl = parseUrlAndArgs(request, targetUrl);
        String contentType = request.getHeader("Content-Type");
        if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
            //Append parameters to url
            Enumeration<String> len = request.getParameterNames();
            StringBuilder sb = new StringBuilder(targetUrl);
            if (!targetUrl.contains("?")) {
                if (len.hasMoreElements()) {
                    String key = len.nextElement();
                    sb.append("?");
                    sb.append(key);
                    sb.append("=");
                    sb.append(request.getParameter(key));
                }
            }
            while (len.hasMoreElements()) {
                sb.append("&");
                String key = len.nextElement();
                sb.append(key);
                sb.append("=");
                sb.append(request.getParameter(key));
            }
            targetUrl = sb.toString();
        }

        log.info("HttpProxyHelper After Parse targetUrl={}", targetUrl);

        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String method = request.getMethod().toUpperCase();
            conn.setRequestMethod(method);
            log.info("HttpProxyHelper request method={}", conn.getRequestMethod());
            //默认超时时间
            conn.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10));
            log.info("HttpProxyHelper ConnectTimeout(Millis)={}", conn.getConnectTimeout());

            //TODO: 暂不支持长连接，后期处理
            // 处理request请求头
            Enumeration headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                String headName = (String) headers.nextElement();//Connection
                String headValue = request.getHeader(headName);//keep-alive
                conn.setRequestProperty(headName, headValue);
            }

            StringBuffer buffer = new StringBuffer();

            // 处理request请求体
            if ("POST".equals(method)) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = null;
                os = conn.getOutputStream();
                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    Enumeration<String> len = request.getParameterNames();
                    String boundry = contentType.split("boundary=")[1];
                    while (len.hasMoreElements()) {
                        String key = len.nextElement();
                        buffer.append("--" + boundry + "\r\n");
                        buffer.append("Content-Disposition: form-data; name=\"");
                        buffer.append(key);
                        buffer.append("\"\r\n\r\n");
                        buffer.append(request.getParameter(key));
                        buffer.append("\r\n");
                    }
                    if (request.getParameterNames().hasMoreElements())
                        buffer.append("--" + boundry + "--\r\n");

                    os.write(buffer.toString().getBytes());
                }
                try (InputStream body = request.getInputStream()) {
                    IOUtils.copy(body, os);
                }
            }

            //建立连接
            conn.connect();

            //请求状态
            int responseCode = conn.getResponseCode();
            response.setStatus(responseCode);
            log.info("HttpProxyHelper responseCode={}", responseCode);

            //处理response请求头
            Map<String, String> httpResponseHeader = getHttpResponseHeader(conn);
            for (Map.Entry<String, String> headerEntry : httpResponseHeader.entrySet()) {
                response.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }

            //处理STATUS: 200
            if (HttpStatus.SC_OK == responseCode) {
                //处理成功请求体
                try (ServletOutputStream outputStream = response.getOutputStream(); InputStream inputStream = conn.getInputStream()) {
                    IOUtils.copy(inputStream, outputStream);
                }
                log.info("HttpProxyHelper Success");
            } else {
                //处理失败请求体
                try (ServletOutputStream outputStream = response.getOutputStream(); InputStream inputStream = conn.getErrorStream()) {
                    IOUtils.copy(inputStream, outputStream);
                }
                log.info("HttpProxyHelper Failed");
            }
        } catch (IOException e) {
            log.error("HttpProxyHelper proxyRequest Exception", e);
            String errorTitle = "Server Error";
            HttpProxyHelper.handleHttpErrorResponse(response, HttpStatus.SC_INTERNAL_SERVER_ERROR, request.getRequestURI(), errorTitle, e.toString(), e.getClass().getSimpleName());
        }
    }

    /**
     * 根据requestQueryString拼装url
     */
    private static String parseUrlAndArgs(HttpServletRequest request, String url) {
        if (url.contains("?")) return url;

        String queryStr = request.getQueryString();
        if (Strings.isNullOrEmpty(queryStr)) return url;

        return url + "?" + queryStr;
    }

    /**
     * HttpURLConnection头信息转换
     */
    private static Map<String, String> getHttpResponseHeader(HttpURLConnection http) throws UnsupportedEncodingException {
        Map<String, String> header = new LinkedHashMap<>();
        for (int i = 0; ; i++) {
            String field = http.getHeaderField(i);
            if (field == null) break; //有效field处理完毕
            String key = http.getHeaderFieldKey(i);
            if (key == null) continue; //忽略statusLine
            header.put(key, field);
        }
        return header;
    }

    /**
     * 统一HTTP请求错误信息格式，json
     *
     * @param response   HttpResponse
     * @param httpStatus HttpStatus eg. HttpStatus.SC_NOT_FOUND
     * @param path       requestUri
     * @param error      error title
     * @param message    error message
     * @param exception  exception type
     */
    public static void handleHttpErrorResponse(HttpServletResponse response, int httpStatus, String path, String error, String message, String exception) {
        response.setStatus(httpStatus);
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        //错误信息
        String content = "{\"timestamp\": %d,\"status\": %d,\"error\": \"%s\"%s,\"message\": \"%s\",\"path\": \"%s\"}";
        //异常信息
        String exceptionInfo = Strings.isNullOrEmpty(exception) ? "" : ",\"exception\": \"" + exception + "\"";

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            String msg = String.format(content, System.currentTimeMillis(), httpStatus, error, exceptionInfo, message, path);
            outputStream.print(msg);
        } catch (Exception e) {
            log.error("HttpProxyHelper handleHttpErrorResponse Exception", e);
        }
    }

}
