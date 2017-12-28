package com.zydcompany.management.service;

import com.zydcompany.management.domain.model.SystemUserDo;
import com.zydcompany.management.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class SystemUserService {
    @Autowired
    SystemUserMapper systemUserMapper;

    public SystemUserDo getSystemUserDoById(BigInteger id) {
        return systemUserMapper.getSystemUserDoById(id);
    }

    public SystemUserDo getSystemUserDoByMobile(String mobile) {
        return systemUserMapper.getSystemUserDoByMobile(mobile);
    }

    public void saveSystemUserDo(SystemUserDo systemUserDo) {
        systemUserMapper.saveSystemUserDo(systemUserDo);
    }

    public void updateSystemUserDo(BigInteger id, String address) {
        systemUserMapper.updateSystemUserDo(id, address);
    }
}
