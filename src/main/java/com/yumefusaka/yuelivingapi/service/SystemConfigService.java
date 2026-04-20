package com.yumefusaka.yuelivingapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yumefusaka.yuelivingapi.pojo.Entity.SystemConfig;

public interface SystemConfigService extends IService<SystemConfig> {
    SystemConfig getByConfigKey(String configKey);
}
