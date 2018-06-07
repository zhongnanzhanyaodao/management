package com.zydcompany.management.util;


import com.zydcompany.management.common.constant.SymbolConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RestTemplateUtil {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    @Autowired
    private RestTemplate restTemplate;

    /**
     * get请求
     */
    public String get(String requestUrl) {
        long start = System.currentTimeMillis();
        try {
            String respStr = restTemplate.getForObject(requestUrl, String.class);
            log.info("requestUrl={} respStr={} costTime={}", requestUrl, respStr, (System.currentTimeMillis() - start));
            return respStr;
        } catch (Exception e) {
            log.error("requestUrl={} costTime={} Exception", requestUrl, (System.currentTimeMillis() - start), e);
        }
        return null;
    }

    /**
     * get请求,入参Map
     */
    public String getByMap(String requestUrl, Map<String, String> requestParams) {
        if (!CollectionUtils.isEmpty(requestParams)) {
            requestUrl = requestUrl + SymbolConstant.QUESTION_MARK;
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                requestUrl = requestUrl + SymbolConstant.SERVEN_UP + entry.getKey() + SymbolConstant.EQUALITY_SIGN + entry.getValue();
            }
        }
        return get(requestUrl);
    }

    /**
     * post请求,入参Object
     */
    public String postByObject(String requestUrl, Object requestParams) {
        Map<String, String> requestMap = BeanMapUtil.bean2Map(requestParams);
        return postByMap(requestUrl, requestMap);
    }

    /**
     * post请求,入参Map
     */
    public String postByMap(String requestUrl, Map<String, String> requestParams) {
        long start = System.currentTimeMillis();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                params.add(entry.getKey(), entry.getValue());
            }
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            String respStr = restTemplate.postForObject(requestUrl, requestEntity, String.class);
            log.info("requestUrl={} requestParams={} respStr={} costTime={}", requestUrl, FastJSONHelper.serialize(requestParams), respStr, (System.currentTimeMillis() - start));
            return respStr;
        } catch (Exception e) {
            log.error("requestUrl={} requestParams={} costTime={} Exception", requestUrl, FastJSONHelper.serialize(requestParams), (System.currentTimeMillis() - start), e);
        }
        return null;
    }


}
