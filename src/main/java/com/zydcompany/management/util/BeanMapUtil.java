package com.zydcompany.management.util;


import com.google.common.collect.Maps;
import com.zydcompany.management.common.constant.SymbolConstant;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

public class BeanMapUtil {

    public static <T> Map<String, String> bean2Map(T bean) {
        Map<String, String> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if (beanMap.get(key) != null) {
                    map.put(key + SymbolConstant.EMPTY_STR, String.valueOf(beanMap.get(key)));
                }
            }

        }
        return map;
    }

    public static <T> T map2Bean(Map<String, String> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }
}
