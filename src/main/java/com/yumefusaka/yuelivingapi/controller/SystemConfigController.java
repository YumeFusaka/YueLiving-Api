package com.yumefusaka.yuelivingapi.controller;

import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleEnum;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.pojo.Entity.SystemConfig;
import com.yumefusaka.yuelivingapi.service.OperationLogService;
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

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public Result<List<SystemConfig>> getConfigs() {
        return Result.success(systemConfigService.list());
    }

    @PostMapping
    public Result<String> addConfig(@RequestBody SystemConfig systemConfig) {
        systemConfigService.save(systemConfig);
        operationLogService.record("系统配置", "新增配置", "system_config", systemConfig.getId(), "新增配置 " + systemConfig.getConfigKey());
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> updateConfig(@RequestBody SystemConfig systemConfig) {
        systemConfigService.updateById(systemConfig);
        operationLogService.record("系统配置", "更新配置", "system_config", systemConfig.getId(), "更新配置 " + systemConfig.getConfigKey());
        return Result.success("更新成功");
    }
}
