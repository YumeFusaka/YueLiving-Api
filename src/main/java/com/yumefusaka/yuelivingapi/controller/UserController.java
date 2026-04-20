package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.pojo.DTO.LoginDTO;
import com.yumefusaka.yuelivingapi.pojo.DTO.RegisterDTO;
import com.yumefusaka.yuelivingapi.pojo.DTO.UserStatusDTO;
import com.yumefusaka.yuelivingapi.pojo.Entity.User;
import com.yumefusaka.yuelivingapi.service.UserService;
import com.yumefusaka.yuelivingapi.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        String account = loginDTO.getAccount() != null && !loginDTO.getAccount().isBlank()
                ? loginDTO.getAccount()
                : loginDTO.getUsername();
        User user = userService.login(account, loginDTO.getPassword());
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
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<List<User>> getUsers() {
        List<User> users = userService.list();
        Long currentRoleId = Long.valueOf(BaseContext.getCurrentRoleId());
        if (currentRoleId.equals(RoleEnum.PROPERTY_MANAGER)) {
            users = users.stream().filter(user -> RoleEnum.OWNER == user.getRoleId()).toList();
        }
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

    @PutMapping("/status")
    @RoleRequired({RoleEnum.PROPERTY_MANAGER, RoleEnum.SYSTEM_ADMIN})
    public Result<String> updateUserStatus(@RequestBody UserStatusDTO dto) {
        if (userService.updateUserStatus(dto.getUserId(), dto.getStatus())) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User user) {
        Long userId = Long.valueOf(BaseContext.getCurrentId());
        User existingUser = userService.getById(userId);
        if (existingUser != null) {
            // 只更新允许的字段
            existingUser.setRealName(user.getRealName());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            if (userService.updateById(existingUser)) {
                return Result.success("更新成功");
            }
        }
        return Result.error("更新失败");
    }

    @PutMapping("/profile/password")
    public Result<String> changePassword(@RequestBody Map<String, String> passwordData) {
        Long userId = Long.valueOf(BaseContext.getCurrentId());
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        if (userService.changePassword(userId, oldPassword, newPassword)) {
            return Result.success("密码修改成功");
        }
        return Result.error("旧密码错误或修改失败");
    }

    @PostMapping("/profile/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File destFile = new File(uploadDir + filename);
            file.transferTo(destFile);

            Long userId = Long.valueOf(BaseContext.getCurrentId());
            User user = userService.getById(userId);
            user.setAvatar("/uploads/avatars/" + filename);
            userService.updateById(user);

            Map<String, String> data = new HashMap<>();
            data.put("url", "/uploads/avatars/" + filename);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error("上传失败");
        }
    }
}
