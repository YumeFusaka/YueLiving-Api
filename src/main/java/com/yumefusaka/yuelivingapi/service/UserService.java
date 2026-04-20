package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    User login(String username, String password);
    boolean register(User user);
    List<User> getMaintenanceUsers();
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    void ensureUserEnabled(User user);
    boolean canManageUser(Long operatorRoleId, Long targetRoleId);
    boolean updateUserStatus(Long userId, Integer status);
}
