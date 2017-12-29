package com.zydcompany.management.config.mybatis.sharding;


import com.zydcompany.management.common.constant.NumberConstant;

public class ShardingConstant {

    //数据库分库数目
    public static final Integer DATA_BASE_SHARDING_COUNT = NumberConstant.TWO;
    //数据库0名称
    public static final String DATA_BASE_ORIGIN_NAME_0 = "management_0";
    //数据库1名称
    public static final String DATA_BASE_ORIGIN_NAME_1 = "management_1";


    //system_user分表数目
    public static final Integer SYSTEM_USER_SHARDING_COUNT = NumberConstant.FIVE;
    //system_user分表依据的字段
    public static final String SYSTEM_USER_SHARDING_COLUMN = "mobile";
    //system_user逻辑表名
    public static final String SYSTEM_USER_LOGICTABLE = "system_user";
    //system_user真实数据节点，由数据源名 + 表名组成，以小数点分隔。
    public static final String SYSTEM_USER_ACTUALDATANODES = "management_${0..1}.system_user_${0..4}";
    //system_user表的数据库分库依据的字段
    public static final String SYSTEM_USER_DATA_BASE_SHARDING_COLUMN = "mobile";

    /*//user_detail_supplement 不分库也不分表，指定数据源为management_0
    //user_detail_supplement逻辑表名
    public static final String USER_DETAIL_SUPPLEMENT_LOGICTABLE = "user_detail_supplement";
    //user_detail_supplement真实数据节点，由数据源名 + 表名组成，以小数点分隔。只保存在management_0的user_detail_supplement表
    public static final String USER_DETAIL_SUPPLEMENT_ACTUALDATANODES = "management_0.user_detail_supplement";*/

    //user_detail_supplement分表数目
    public static final Integer USER_DETAIL_SUPPLEMENT_COUNT = NumberConstant.THREE;
    //user_detail_supplement分表依据的字段
    public static final String USER_DETAIL_SUPPLEMENT_COLUMN = "user_id";
    //user_detail_supplement逻辑表名
    public static final String USER_DETAIL_SUPPLEMENT_LOGICTABLE = "user_detail_supplement";
    //user_detail_supplement真实数据节点，由数据源名 + 表名组成，以小数点分隔。只保存在management_0库，分表处理
    public static final String USER_DETAIL_SUPPLEMENT_ACTUALDATANODES = "management_0.user_detail_supplement_${0..2}";

}
