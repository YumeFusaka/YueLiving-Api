package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.mapper.UserMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(query -> query.eq(User::getUsername, username).or().eq(User::getPhone, username));
        User user = userMapper.selectOne(wrapper);
        if (user != null && password.equals(user.getPassword())) {
            ensureUserEnabled(user);
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);
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

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user != null && oldPassword.equals(user.getPassword())) {
            user.setPassword(newPassword);
            return userMapper.updateById(user) > 0;
        }
        return false;
    }

    @Override
    public void ensureUserEnabled(User user) {
        if (user == null || user.getStatus() == null || user.getStatus() == 0) {
            throw new RuntimeException("账号已禁用");
        }
    }

    @Override
    public boolean canManageUser(Long operatorRoleId, Long targetRoleId) {
        if (operatorRoleId == null || targetRoleId == null) {
            return false;
        }
        if (operatorRoleId.equals(RoleEnum.SYSTEM_ADMIN)) {
            return targetRoleId.equals(RoleEnum.PROPERTY_MANAGER);
        }
        return operatorRoleId.equals(RoleEnum.PROPERTY_MANAGER) && targetRoleId.equals(RoleEnum.OWNER);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }
}
