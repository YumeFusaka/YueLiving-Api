package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.pojo.DTO.LoginDTO;
import com.yumefusaka.yuelivingapi.pojo.DTO.RegisterDTO;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.UserService;
import com.yumefusaka.yuelivingapi.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
        // 如果没有指定角色，默认设置为业主
        if (user.getRoleId() == null) {
            user.setRoleId(RoleEnum.OWNER);
        }
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

    @GetMapping
    @RoleRequired({RoleEnum.SYSTEM_ADMIN})
    public Result<List<User>> getUsers() {
        List<User> users = userService.list();
        // 清除密码信息
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    @GetMapping("/maintenance")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<List<User>> getMaintenanceUsers() {
        List<User> users = userService.getMaintenanceUsers();
        // 清除密码信息
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    @PostMapping
    @RoleRequired({RoleEnum.SYSTEM_ADMIN})
    public Result<String> addUser(@RequestBody User user) {
        if (userService.save(user)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    @PutMapping
    @RoleRequired({RoleEnum.SYSTEM_ADMIN})
    public Result<String> updateUser(@RequestBody User user) {
        if (userService.updateById(user)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @RoleRequired({RoleEnum.SYSTEM_ADMIN})
    public Result<String> deleteUser(@PathVariable Long id) {
        if (userService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}