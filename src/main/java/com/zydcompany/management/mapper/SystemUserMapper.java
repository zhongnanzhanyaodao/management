package com.zydcompany.management.mapper;


import com.zydcompany.management.domain.model.SystemUserDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

public interface SystemUserMapper {

    SystemUserDo getSystemUserDoById(BigInteger id);

    SystemUserDo getSystemUserDoByMobile(String mobile);

    void saveSystemUserDo(SystemUserDo systemUserDo);

    void updateSystemUserDo(@Param("id") BigInteger id,
                            @Param("address") String address);
}
