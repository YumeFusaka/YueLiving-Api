package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
    boolean register(User user);
}