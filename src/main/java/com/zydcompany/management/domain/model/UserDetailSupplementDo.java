package com.zydcompany.management.domain.model;

import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
public class UserDetailSupplementDo {
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
     * 用户id
     */
    private BigInteger userId;
}
