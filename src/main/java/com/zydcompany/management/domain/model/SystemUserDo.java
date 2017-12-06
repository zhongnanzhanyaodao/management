package com.zydcompany.management.domain.model;

import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class SystemUserDo {
    /**
     * 主键
     */
    private BigInteger id;
    /**
     * 创建时间
     */
    private Timestamp gmtCreate;
    /**
     * 修改时间
     */
    private Timestamp gmtModified;
    /**
     * 姓名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 性别(0-男 1-女)
     */
    private Integer sex;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 地址
     */
    private String address;
}
