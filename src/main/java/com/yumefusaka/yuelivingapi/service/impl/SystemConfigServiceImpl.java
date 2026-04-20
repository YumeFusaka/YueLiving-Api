package com.yumefusaka.yuelivingapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yumefusaka.yuelivingapi.mapper.SystemConfigMapper;
import com.yumefusaka.yuelivingapi.pojo.Entity.SystemConfig;
import com.yumefusaka.yuelivingapi.service.SystemConfigService;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Override
    public SystemConfig getByConfigKey(String configKey) {
        return getOne(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, configKey));
    }
}
