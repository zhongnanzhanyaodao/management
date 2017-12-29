package com.zydcompany.management.service;

import com.zydcompany.management.domain.model.SystemUserDo;
import com.zydcompany.management.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemUserService {
    @Autowired
    SystemUserMapper systemUserMapper;

    public SystemUserDo getSystemUserDoByMobile(String mobile) {
        return systemUserMapper.getSystemUserDoByMobile(mobile);
    }

    public void saveSystemUserDo(SystemUserDo systemUserDo) {
        systemUserMapper.saveSystemUserDo(systemUserDo);
    }

    public void updateSystemUserDoByMobile(String mobile, String address) {
        systemUserMapper.updateSystemUserDoByMobile(mobile, address);
    }

    public void deleteSystemUserDoByMobile(String mobile) {
        systemUserMapper.deleteSystemUserDoByMobile(mobile);
    }


   /* public SystemUserDo getSystemUserDoById(BigInteger id) {
        return systemUserMapper.getSystemUserDoById(id);
    }*/

    /*public void updateSystemUserDoById(BigInteger id, String address) {
        systemUserMapper.updateSystemUserDoById(id, address);
    }*/

   /* public void deleteSystemUserDoById(BigInteger id) {
        systemUserMapper.deleteSystemUserDoById(id);
    }*/


}
