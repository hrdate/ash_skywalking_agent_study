package org.example.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.app.entity.UserEntity;
import org.example.app.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/23 10:04 PM
 */
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<UserEntity> selectUserList(String userName) {
        LambdaQueryWrapper<UserEntity> lqw = new LambdaQueryWrapper<>();
        lqw.like(UserEntity::getUserName, userName);
        List<UserEntity> userEntityList = userMapper.selectList(lqw);
        log.info("selectList result:{}", userEntityList);
        return userEntityList;
    }
}
