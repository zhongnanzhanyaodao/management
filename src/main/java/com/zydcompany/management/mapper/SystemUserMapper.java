package com.zydcompany.management.mapper;


import com.zydcompany.management.domain.model.SystemUserDo;

import java.math.BigInteger;

public interface SystemUserMapper {

    SystemUserDo getSystemUserDoById(BigInteger id);
}
