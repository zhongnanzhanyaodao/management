package com.zydcompany.management.service;

import com.zydcompany.management.domain.model.UserDetailSupplementDo;
import com.zydcompany.management.mapper.UserDetailSupplementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailSupplementService {
    @Autowired
    UserDetailSupplementMapper userDetailSupplementMapper;


    public void saveUserDetailSupplementDo(UserDetailSupplementDo userDetailSupplementDo) {
        userDetailSupplementMapper.saveUserDetailSupplementDo(userDetailSupplementDo);
    }
}
