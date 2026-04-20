package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.pojo.Entity.SystemConfig;
import com.yumefusaka.yuelivingapi.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system-config")
@RoleRequired({RoleEnum.SYSTEM_ADMIN})
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public Result<List<SystemConfig>> getConfigs() {
        return Result.success(systemConfigService.list());
    }

    @PostMapping
    public Result<String> addConfig(@RequestBody SystemConfig systemConfig) {
        systemConfigService.save(systemConfig);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> updateConfig(@RequestBody SystemConfig systemConfig) {
        systemConfigService.updateById(systemConfig);
        return Result.success("更新成功");
    }
}
