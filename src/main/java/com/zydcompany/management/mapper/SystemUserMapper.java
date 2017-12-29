package com.zydcompany.management.mapper;


import com.zydcompany.management.domain.model.SystemUserDo;
import org.apache.ibatis.annotations.Param;

public interface SystemUserMapper {
    //禁用,请使用SHARDING_COLUMN
//    SystemUserDo getSystemUserDoById(BigInteger id);

    SystemUserDo getSystemUserDoByMobile(String mobile);

    void saveSystemUserDo(SystemUserDo systemUserDo);

    //禁用,请使用SHARDING_COLUMN
//    void updateSystemUserDoById(@Param("id") BigInteger id,@Param("address") String address);

    void updateSystemUserDoByMobile(@Param("mobile") String mobile, @Param("address") String address);

    //禁用,请使用SHARDING_COLUMN
//    void deleteSystemUserDoById(BigInteger id);

    void deleteSystemUserDoByMobile(String mobile);
}
