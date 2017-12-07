package com.zydcompany.management.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.zydcompany.management.common.constant.NumberConstant;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class FastJSONHelper {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    /**
     * 将java类型的对象转换为JSON格式的字符串
     *
     * @param object java类型的对象
     * @return JSON格式的字符串
     */
    public static <T> String serialize(T object) {
        return JSON.toJSONString(object);
    }

    /**
     * 将java类型的对象转换为JSON格式的字符串， 可定制输出格式
     *
     * @param object
     * @param serializerFeature
     * @return
     */
    public static <T> String serialize(T object, SerializerFeature serializerFeature) {
        return JSON.toJSONString(object, serializerFeature);
    }

    /**
     * 将java类型的对象转换为JSON格式的字符串， 可定制输出字段
     *
     * @param object
     * @param simplePropertyPreFilter
     * @return
     */
    public static <T> String serialize(T object, SimplePropertyPreFilter simplePropertyPreFilter) {
        return JSON.toJSONString(object, simplePropertyPreFilter);
    }

    /**
     * 将java类型的对象转换为JSON格式的字符串， 可定制输出字段
     *
     * @param object
     * @param show
     * @param fieldNames
     * @return
     */
    public static <T> String serialize(T object, Boolean show, String... fieldNames) {
        try {
            if (object == null) {
                return null;
            }
            SimplePropertyPreFilter filter = null;
            if (show) {
                filter = new SimplePropertyPreFilter(object.getClass(), fieldNames);
            } else {
                String[] fieldDestNames = {};
                if (fieldNames != null && fieldNames.length > NumberConstant.ZERO) {
                    Class cls = object.getClass();
                    Field[] fields = cls.getDeclaredFields();
                    fieldDestNames = new String[fields.length];
                    for (int i = NumberConstant.ZERO; i < fields.length; i++) {
                        boolean flag = true;
                        for (int j = NumberConstant.ZERO; j < fieldNames.length; j++) {
                            if (fieldNames[j].equals(fields[i].getName())) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            fieldDestNames[i] = fields[i].getName();
                        }
                    }
                }
                filter = new SimplePropertyPreFilter(object.getClass(), fieldDestNames);
            }
            return serialize(object, filter);
        } catch (Exception e) {
            log.error("FastJSONHelper serialize error!", e);
        }
        return null;
    }

    /**
     * 将JSON格式的字符串转换为java类型的对象或者java数组类型的对象，不包括java集合类型
     *
     * @param json JSON格式的字符串
     * @param clz  java类型或者java数组类型，不包括java集合类型
     * @return java类型的对象或者java数组类型的对象，不包括java集合类型的对象
     */
    public static <T> T deserialize(String json, Class<T> clz) {
        return JSON.parseObject(json, clz);
    }

    /**
     * 将JSON格式的字符串转换为List<T>类型的对象
     *
     * @param json JSON格式的字符串
     * @param clz  指定泛型集合里面的T类型
     * @return List<T>类型的对象
     */
    public static <T> List<T> deserializeList(String json, Class<T> clz) {
        return JSON.parseArray(json, clz);
    }

    /**
     * 将JSON格式的字符串转换成任意Java类型的对象
     *
     * @param json JSON格式的字符串
     * @param type 任意Java类型
     * @return 任意Java类型的对象
     */
    public static <T> T deserializeAny(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }


    public static String getJsonValue(String json, String key) {
        HashMap map = FastJSONHelper.deserialize(json, HashMap.class);
        return (String) map.get(key);
    }

}
