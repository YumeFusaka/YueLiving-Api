package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.mapper.UserMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            return false;
        }
        // 如果没有指定角色，默认设置为业主
        if (user.getRoleId() == null || user.getRoleId() == 0) {
            user.setRoleId(RoleEnum.OWNER);
        }
        user.setStatus(1);
        return userMapper.insert(user) > 0;
    }

    @Override
    public List<User> getMaintenanceUsers() {
        // 获取物业管理员和系统管理员作为维修人员
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getRoleId, RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN);
        wrapper.eq(User::getStatus, 1); // 只获取启用状态的用户
        return userMapper.selectList(wrapper);
    }
}