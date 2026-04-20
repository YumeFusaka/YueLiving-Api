package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.pojo.DTO.LoginDTO;
import com.yumefusaka.yuelivingapi.pojo.DTO.RegisterDTO;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.UserService;
import com.yumefusaka.yuelivingapi.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (user != null) {
            String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRoleId());
            user.setPassword(null);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            return Result.success(data);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        if (userService.register(user)) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }

    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = Long.valueOf(BaseContext.getCurrentId());
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User user) {
        Long userId = Long.valueOf(com.yumefusaka.yuelivingapi.common.context.BaseContext.getCurrentId());
        user.setId(userId);
        userService.updateById(user);
        return Result.success("更新成功");
    }
}